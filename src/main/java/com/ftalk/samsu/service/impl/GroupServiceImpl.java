package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.group.GroupImportMemberResponse;
import com.ftalk.samsu.payload.group.MemberImportFailed;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.repository.GroupRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupServiceImpl.class);
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public Group getGroupById(Integer id) {
        return groupRepository.findById(id).orElseThrow(() -> new BadRequestException("Group not found"));
    }

    @Override
    public Group getGroupByName(String name) {
        return groupRepository.findByName(name).orElseThrow(() -> new BadRequestException("Group not found"));
    }

    @Override
    public Group updateGroup(GroupRequest groupRequest, Integer id) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Group not found"));
        Set<User> listUser = userRepository.findAllByRollnumberIn(groupRequest.getUserRollnumbers());
        existingGroup.setName(groupRequest.getName());
        existingGroup.setUsers(listUser);
        groupRepository.save(existingGroup);
        return null;
    }

    @Override
    public GroupImportMemberResponse addMemberToExistGroup(GroupRequest groupRequest, Integer id) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Group not found. Id: " + id));
        Set<User> listUser = existingGroup.getUsers();
        Set<String> listUserRollnumber = listUser.stream()
                .map(User::getRollnumber)
                .collect(Collectors.toSet());
        List<MemberImportFailed> memberImportFailedList = new ArrayList<>();
        for (String rollnumber : groupRequest.getUserRollnumbers()) {
            try {
                if (listUserRollnumber.contains(rollnumber)){
                    memberImportFailedList.add(new MemberImportFailed(rollnumber, "Rollnumber already exist in this group."));
                    continue;
                }
                Optional<User> user = userRepository.findByRollnumber(rollnumber);
                if (!user.isPresent()){
                    memberImportFailedList.add(new MemberImportFailed(rollnumber, "Rollnumber not found."));
                    continue;
                }
                listUser.add(user.get());
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                memberImportFailedList.add(new MemberImportFailed(rollnumber, ex.getMessage()));
            }
        }
        existingGroup.setUsers(listUser);
        groupRepository.save(existingGroup);
        int amount = groupRequest.getUserRollnumbers().size();
        int failed = memberImportFailedList.size();
        int success = amount - failed;
        return new GroupImportMemberResponse(groupRequest.getUserRollnumbers().size(),success, failed, memberImportFailedList);
    }

    @Override
    public Set<User> getGroupMembersById(Integer id) {
        Group group = groupRepository.findById(id).orElseThrow(() -> new BadRequestException("Group ID not found"));
        return group.getUsers();
    }

    @Override
    public Group addGroup(GroupRequest groupRequest, UserPrincipal userPrincipal) {
        validateGroup(groupRequest);
        Set<User> users = userRepository.findAllByRollnumberIn(groupRequest.getUserRollnumbers());
        Group group = new Group(groupRequest.getName(), users);
        return groupRepository.save(group);
    }

    @Override
    public ApiResponse deleteGroup(Integer groupID) {
        groupRepository.deleteById(groupID);
        return new ApiResponse(Boolean.TRUE, "You successfully deleted group: " + groupID);
    }

    private void validateGroup(GroupRequest groupRequest) {
        if (groupRepository.existsByName(groupRequest.getName())) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Group name is already taken");
            throw new BadRequestException(apiResponse);
        }
    }

}

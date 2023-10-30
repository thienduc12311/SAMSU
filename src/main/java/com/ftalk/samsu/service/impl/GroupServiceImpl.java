package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.Category;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.PostResponse;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.repository.CategoryRepository;
import com.ftalk.samsu.repository.GroupRepository;
import com.ftalk.samsu.repository.PostRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GroupService;
import com.ftalk.samsu.service.PostService;
import com.ftalk.samsu.utils.AppConstants;
import com.ftalk.samsu.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static com.ftalk.samsu.utils.AppConstants.*;

@Service
public class GroupServiceImpl implements GroupService {
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

    private void validateGroup(GroupRequest groupRequest) {
        if (groupRepository.existsByName(groupRequest.getName())) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Group name is already taken");
            throw new BadRequestException(apiResponse);
        }
    }
}

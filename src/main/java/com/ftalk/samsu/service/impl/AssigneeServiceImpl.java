package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.AssigneeRequest;
import com.ftalk.samsu.payload.event.AssigneeResponse;
import com.ftalk.samsu.repository.AssigneeRepository;
import com.ftalk.samsu.repository.TaskRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.AssigneeService;
import com.ftalk.samsu.service.UserService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import com.ftalk.samsu.utils.event.AssigneeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ftalk.samsu.utils.AppConstants.CREATED_AT;

@Service
public class AssigneeServiceImpl implements AssigneeService {
    @Autowired
    private AssigneeRepository assigneeRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public Boolean updateAssigneeTask(Integer taskId, Set<String> rollnumber, List<AssigneeRequest> assigneeRequestList) {
        Map<String, User> assigneeUser = userService.getMapUserByRollnumber(rollnumber);
        List<Assignee> assigneeList = getListAssigneeOfTask(taskId);
//        List<AssigneeId> newAssigneeList = rollnumber.parallelStream().map((e) -> new AssigneeId(taskId, assigneeUser.get(e).getId())).collect(Collectors.toList());
        List<Integer> newAssigneeList = rollnumber.parallelStream().map((e) -> assigneeUser.get(e).getId()).collect(Collectors.toList());

        //delete cac phan tu khong con su dung
//        assigneeRepository.deleteAll(assigneeList.parallelStream().filter(e -> !newAssigneeList.contains(e.getId())).collect(Collectors.toList()));
        assigneeRepository.deleteAll(assigneeList.parallelStream().filter(e -> !newAssigneeList.contains(e.getId().getUsersId())).collect(Collectors.toList()));

        //add va update cac assignee object
        for (AssigneeRequest assigneeRequest : assigneeRequestList) {
            Assignee assignee = new Assignee(new AssigneeId(taskId, assigneeUser.get(assigneeRequest.getRollnumber()).getId()), assigneeRequest.getStatus());
            assigneeRepository.save(assignee);
        }

        return Boolean.TRUE;
    }

    public List<Assignee> getListAssigneeOfTask(Integer taskId) {
        return assigneeRepository.findByIdTasksId(taskId);
    }

    @Override
    public ApiResponse addAssigneeTaskWithList(Integer taskId, List<AssigneeRequest> assigneeRequestList) {
        Set<String> rollnumber = assigneeRequestList.parallelStream().map(AssigneeRequest::getRollnumber)
                .collect(Collectors.toSet());
        Map<String, User> assigneeUser = userService.getMapUserByRollnumber(rollnumber);
        for (AssigneeRequest assigneeRequest : assigneeRequestList) {
            Assignee assignee = new Assignee(new AssigneeId(taskId, assigneeUser.get(assigneeRequest.getRollnumber()).getId()), assigneeRequest.getStatus());
            assigneeRepository.save(assignee);
        }
        return new ApiResponse(Boolean.TRUE, "Add list assignee of task success");
    }

    @Override
    public ApiResponse deleteAssigneeTaskWithList(Integer taskId, Set<String> rollnumbers) {
        Map<String, User> assigneeUser = userService.getMapUserByRollnumber(rollnumbers);
        List<AssigneeId> assigneeIds = rollnumbers.parallelStream().map(rollnumber ->
                        new AssigneeId(taskId, assigneeUser.get(rollnumber).getId()))
                .collect(Collectors.toList());
        assigneeRepository.deleteAllByAssigneeId(assigneeIds);
        return new ApiResponse(Boolean.TRUE, "Delete list assignee of task success");
    }

    @Override
    public ApiResponse deleteAssigneeTask(Integer taskId, String rollnumbers) {
        User user = userRepository.getUserByRollnumber(rollnumbers);
        assigneeRepository.deleteById(new AssigneeId(taskId, user.getId()));
        return new ApiResponse(Boolean.TRUE, "Delete Assignee of task success");
    }
    @Override
    public AssigneeResponse findTaskById(Integer taskId, UserPrincipal currentUser) {
        Assignee assignee = assigneeRepository.findById(new AssigneeId(taskId, currentUser.getId())).orElseThrow(
                () ->  new BadRequestException("EventId not found!!")
        );
        return new AssigneeResponse(assignee);
    }

    @Transactional
    @Override
    public ApiResponse updateAssigneeStatus(Integer taskId, Short status, UserPrincipal currentUser) {
        Assignee assignee = assigneeRepository.findById(new AssigneeId(taskId, currentUser.getId())).orElseThrow(
                () -> new ResourceNotFoundException("Assignee", "taskId", taskId)
        );
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))) {
            assignee.setStatus(status);
            assigneeRepository.save(assignee);
            return new ApiResponse(Boolean.TRUE, "You successfully updated assignee");
        } else if (AssigneeConstants.ACCEPT.getValue() == assignee.getStatus()
                || AssigneeConstants.REJECT.getValue() == assignee.getStatus()
                || AssigneeConstants.COMPLETE.getValue() == assignee.getStatus()) {
            assignee.setStatus(status);
            assigneeRepository.save(assignee);
            return new ApiResponse(Boolean.TRUE, "You successfully updated assignee");
        }
        throw new UnauthorizedException("You don't have permission to update this");
    }

    @Transactional
    @Override
    public ApiResponse updateAssigneeStatus(Integer taskId, String rollnumber, Short status, UserPrincipal currentUser) {
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))) {
            User user = userRepository.getUserByRollnumber(rollnumber);
            Assignee assignee = assigneeRepository.findById(new AssigneeId(taskId, user.getId())).orElseThrow(
                    () -> new ResourceNotFoundException("Assignee", "taskId", taskId)
            );
            assignee.setStatus(status);
            assigneeRepository.save(assignee);
            return new ApiResponse(Boolean.TRUE, "You successfully updated assignee");
        }
        throw new UnauthorizedException("You don't have permission to update this");
    }


    @Override
    public PagedResponse<AssigneeResponse> getAllMyTasks(int page, int size, UserPrincipal userPrincipal) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<Assignee> assignees = assigneeRepository.findByIdUsersId(userPrincipal.getId(), pageable);

        List<Assignee> content = assignees.getNumberOfElements() == 0 ? Collections.emptyList() : assignees.getContent();
        return new PagedResponse<>(ListConverter.listToList(content, AssigneeResponse::new), assignees.getNumber(), assignees.getSize(), assignees.getTotalElements(),
                assignees.getTotalPages(), assignees.isLast());
    }
}
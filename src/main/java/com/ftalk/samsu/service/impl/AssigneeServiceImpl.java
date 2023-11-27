package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.AssigneeRequest;
import com.ftalk.samsu.payload.event.AssigneeResponse;
import com.ftalk.samsu.repository.AssigneeRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.AssigneeService;
import com.ftalk.samsu.service.UserService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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


    public ApiResponse updateAssigneeStatus(Integer taskId, Short status, UserPrincipal userPrincipal) {
        Assignee assignee = assigneeRepository.findById(new AssigneeId(taskId, userPrincipal.getId())).orElseThrow(
                () -> new ResourceNotFoundException("Assignee", "taskId", taskId)
        );
        assignee.setStatus(status);
        assigneeRepository.save(assignee);
        return new ApiResponse(Boolean.TRUE, "You successfully updated assignee");
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

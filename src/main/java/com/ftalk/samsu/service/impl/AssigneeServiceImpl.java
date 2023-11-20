package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.event.AssigneeRequest;
import com.ftalk.samsu.repository.AssigneeRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AssigneeServiceImpl {
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

}

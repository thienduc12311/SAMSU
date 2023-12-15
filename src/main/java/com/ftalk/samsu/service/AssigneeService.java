package com.ftalk.samsu.service;

import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.AssigneeRequest;
import com.ftalk.samsu.payload.event.AssigneeResponse;
import com.ftalk.samsu.payload.event.EventResponse;
import com.ftalk.samsu.payload.event.TaskRequest;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;
import java.util.Set;

public interface AssigneeService {
    ApiResponse updateAssigneeStatus(Integer taskId, Short status, UserPrincipal userPrincipal);

    ApiResponse updateAssigneeStatus(Integer taskId, Integer userId, Short status, UserPrincipal userPrincipal);

    PagedResponse<AssigneeResponse> getAllMyTasks(int page, int size, UserPrincipal userPrincipal);

    ApiResponse deleteAssigneeTaskWithList(Integer taskId, Set<String> rollnumbers);

    ApiResponse deleteAssigneeTask(Integer taskId, String rollnumbers);

    ApiResponse addAssigneeTaskWithList(Integer taskId, List<AssigneeRequest> assigneeRequestList);
}
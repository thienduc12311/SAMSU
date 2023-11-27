package com.ftalk.samsu.service;

import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.event.TaskRequest;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.Set;

public interface AssigneeService {
	ApiResponse updateAssigneeStatus(Integer taskId, Short status, UserPrincipal userPrincipal);
}
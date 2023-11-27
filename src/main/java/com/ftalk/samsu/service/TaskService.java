package com.ftalk.samsu.service;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.EventCreateRequest;
import com.ftalk.samsu.payload.event.TaskRequest;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.Set;

public interface TaskService {
	Task findTaskByEvent(Integer eventId, UserPrincipal currentUser);

	Task createTask(TaskRequest taskRequest, UserPrincipal currentUser);

	Set<String> getTaskStaff(Integer taskId);

	Integer getTaskIdByTitle(Integer eventId, String title);

	Boolean isTaskStaff(Integer taskId, Integer userId);

	Boolean checkPermissionCheckIn(Integer eventId, Integer userId);

	Task updateTask(Integer id, TaskRequest taskRequest, UserPrincipal currentUser);
}
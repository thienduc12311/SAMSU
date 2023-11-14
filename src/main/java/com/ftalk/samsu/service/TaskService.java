package com.ftalk.samsu.service;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.EventCreateRequest;
import com.ftalk.samsu.payload.event.TaskRequest;
import com.ftalk.samsu.security.UserPrincipal;

public interface TaskService {
	Task createTask(TaskRequest taskRequest, UserPrincipal currentUser);

}
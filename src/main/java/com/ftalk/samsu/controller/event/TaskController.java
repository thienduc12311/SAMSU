package com.ftalk.samsu.controller.event;

import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.*;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.AssigneeService;
import com.ftalk.samsu.service.EventProposalService;
import com.ftalk.samsu.service.TaskService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    AssigneeService assigneeService;

    @PutMapping("/{taskId}/assignee/{status}")
    public ResponseEntity<ApiResponse> updateAssignee(@PathVariable(value = "taskId") Integer taskId,
                                                      @PathVariable(value = "status") Short status,
                                                      @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = assigneeService.updateAssigneeStatus(taskId, status, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<PagedResponse<AssigneeResponse>> getAllMyAssignee(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @CurrentUser UserPrincipal currentUser) {
        PagedResponse<AssigneeResponse> apiResponse = assigneeService.getAllMyTasks(page, size, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}

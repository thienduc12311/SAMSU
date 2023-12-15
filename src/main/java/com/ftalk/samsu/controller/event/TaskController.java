package com.ftalk.samsu.controller.event;

import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.model.event.Task;
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
import java.util.List;

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

    @PutMapping("/{taskId}/users/{rollnumber}/assignee/{status}")
    public ResponseEntity<ApiResponse> updateAssignee(@PathVariable(value = "taskId") Integer taskId,
                                                      @PathVariable(value = "rollnumber") String rollnumber,
                                                      @PathVariable(value = "status") Short status,
                                                      @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = assigneeService.updateAssigneeStatus(taskId, rollnumber, status, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<TaskResponse> postTask(
            @Valid @RequestBody TaskRequest taskRequest,
            @CurrentUser UserPrincipal currentUser) {
        Task task = taskService.createTask(taskRequest, currentUser);
        return new ResponseEntity<>(new TaskResponse(task), HttpStatus.OK);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> putTask(
            @PathVariable(value = "taskId") Integer taskId,
            @Valid @RequestBody TaskRequest taskRequest,
            @CurrentUser UserPrincipal currentUser) {
        Task task = taskService.updateTask(taskId, taskRequest, currentUser);
        return new ResponseEntity<>(new TaskResponse(task), HttpStatus.OK);
    }

    @PutMapping("/{taskId}/status/{status}")
    public ResponseEntity<Boolean> putTask(
            @PathVariable(value = "taskId") Integer taskId,
            @PathVariable(value = "status") Short status,
            @CurrentUser UserPrincipal currentUser) {
        Boolean updateTaskStatus = taskService.updateTaskStatus(taskId, status, currentUser);
        return new ResponseEntity<>(updateTaskStatus, HttpStatus.OK);
    }


    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> deleteTask(
            @PathVariable(value = "taskId") Integer taskId,
            @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = taskService.deleteTask(taskId, currentUser);
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

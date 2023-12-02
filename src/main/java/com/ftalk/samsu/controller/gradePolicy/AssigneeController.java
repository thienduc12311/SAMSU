package com.ftalk.samsu.controller.gradePolicy;

import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.event.AssigneeRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeResponse;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.AssigneeService;
import com.ftalk.samsu.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/assignees")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
public class AssigneeController {
    @Autowired
    private AssigneeService assigneeService;

    @PostMapping("/task/{taskId}/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> postAssignee(@PathVariable(name = "taskId") Integer taskId,
                                                               @Valid @RequestBody List<AssigneeRequest> assigneeRequestList,
                                                               @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = assigneeService.addAssigneeTaskWithList(taskId, assigneeRequestList);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/task/{taskId}/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> deleteListAssignee(@PathVariable(name = "taskId") Integer taskId,
                                                    @Valid @RequestBody Set<String> rollnumbers,
                                                    @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = assigneeService.deleteAssigneeTaskWithList(taskId, rollnumbers);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @DeleteMapping("/task/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> deleteAssignee(@PathVariable(name = "taskId") Integer taskId,
                                                      @RequestParam String rollnumber,
                                                      @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = assigneeService.deleteAssigneeTask(taskId, rollnumber);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}

package com.ftalk.samsu.controller.group;

import com.ftalk.samsu.exception.ResponseEntityErrorException;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.group.GroupImportMemberResponse;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @ExceptionHandler(ResponseEntityErrorException.class)
    public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
        return exception.getApiResponse();
    }

    @GetMapping
    public ResponseEntity<List<Group>> getGroup() {
        List<Group> groups = groupService.getAllGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/{groupID}")
    public ResponseEntity<Group> getGroupById(@PathVariable(value = "groupID") Integer id) {
        Group groups = groupService.getGroupById(id);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @PutMapping("/{groupID}")
    public ResponseEntity<Group> updateGroup(@Valid @RequestBody GroupRequest groupRequest, @PathVariable(value = "groupID") Integer id) {
        Group group = groupService.updateGroup(groupRequest, id);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PutMapping("/{groupID}/list")
    public ResponseEntity<GroupImportMemberResponse> addListMemberToGroup(@Valid @RequestBody GroupRequest groupRequest, @PathVariable(value = "groupID") Integer id) {
        GroupImportMemberResponse groupImportMemberResponse = groupService.addMemberToExistGroup(groupRequest, id);
        return new ResponseEntity<>(groupImportMemberResponse, HttpStatus.OK);
    }


    @DeleteMapping("/{groupID}")
    public ResponseEntity<ApiResponse> deleteGroup(@PathVariable(value = "groupID") Integer id) {
        ApiResponse apiResponse = groupService.deleteGroup(id);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/name/{groupName}")
    public ResponseEntity<Group> getGroupByName(@PathVariable(value = "groupName") String name) {
        Group groups = groupService.getGroupByName(name);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Group> addGroup(@Valid @RequestBody GroupRequest group, @CurrentUser UserPrincipal currentUser) {
        Group rs = groupService.addGroup(group, currentUser);
        return new ResponseEntity<>(rs,HttpStatus.OK);
    }


}

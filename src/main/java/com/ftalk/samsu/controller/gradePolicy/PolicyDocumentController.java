package com.ftalk.samsu.controller.gradePolicy;

import com.ftalk.samsu.exception.ResponseEntityErrorException;
import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.PolicyDocumentRequest;
import com.ftalk.samsu.payload.group.GroupImportMemberResponse;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GradePolicyService;
import com.ftalk.samsu.service.GroupService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/policyDocuments")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
public class PolicyDocumentController {

    @Autowired
    private GradePolicyService gradePolicyService;

    @GetMapping
    public ResponseEntity<PagedResponse<PolicyDocument>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<PolicyDocument> response = gradePolicyService.getAllPolicyDocuments(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PolicyDocument> create(@Valid @RequestBody PolicyDocumentRequest policyDocumentRequest,
                                                 @CurrentUser UserPrincipal currentUser) {
        PolicyDocument policyDocument = gradePolicyService.addPolicyDocument(policyDocumentRequest, currentUser);
        return new ResponseEntity<>(policyDocument, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyDocument> get(@PathVariable(name = "id") Integer id, @CurrentUser UserPrincipal currentUser) {
        PolicyDocument policyDocument = gradePolicyService.getPolicyDocument(id, currentUser);
        return new ResponseEntity<>(policyDocument, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolicyDocument> update(@PathVariable(name = "id") Integer id,
                                                 @Valid @RequestBody PolicyDocumentRequest policyDocumentRequest, @CurrentUser UserPrincipal currentUser) {
        PolicyDocument policyDocument = gradePolicyService.updatePolicyDocument(id, policyDocumentRequest, currentUser);
        return new ResponseEntity<>(policyDocument, HttpStatus.OK);
    }

}

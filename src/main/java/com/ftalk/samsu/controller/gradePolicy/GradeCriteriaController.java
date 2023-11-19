package com.ftalk.samsu.controller.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeCriteriaRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeCriteriaResponse;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GradePolicyService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/gradeCriterias")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
public class GradeCriteriaController {

    @Autowired
    private GradePolicyService gradePolicyService;

    @GetMapping
    public ResponseEntity<PagedResponse<GradeCriteriaResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<GradeCriteriaResponse> response = gradePolicyService.getAllGradeCriterias(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GradeCriteriaResponse> create(@Valid @RequestBody GradeCriteriaRequest policyDocumentRequest,
                                                 @CurrentUser UserPrincipal currentUser) {
        GradeCriteria gradeCriteria = gradePolicyService.addGradeCriteria(policyDocumentRequest, currentUser);
        return new ResponseEntity<>(new GradeCriteriaResponse(gradeCriteria), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeCriteriaResponse> get(@PathVariable(name = "id") Integer id, @CurrentUser UserPrincipal currentUser) {
        GradeCriteria gradeCriteria = gradePolicyService.getGradeCriteria(id, currentUser);
        return new ResponseEntity<>(new GradeCriteriaResponse(gradeCriteria), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeCriteriaResponse> update(@PathVariable(name = "id") Integer id,
                                                 @Valid @RequestBody GradeCriteriaRequest policyDocumentRequest, @CurrentUser UserPrincipal currentUser) {
        GradeCriteria gradeCriteria = gradePolicyService.updateGradeCriteria(id, policyDocumentRequest, currentUser);
        return new ResponseEntity<>(new GradeCriteriaResponse(gradeCriteria), HttpStatus.OK);
    }

}

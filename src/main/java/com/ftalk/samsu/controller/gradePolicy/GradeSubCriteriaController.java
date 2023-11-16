package com.ftalk.samsu.controller.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeSubCriteriaRequest;
import com.ftalk.samsu.payload.gradePolicy.PolicyDocumentRequest;
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
@RequestMapping("/api/gradeSubCriterias")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
public class GradeSubCriteriaController {

    @Autowired
    private GradePolicyService gradePolicyService;

    @GetMapping
    public ResponseEntity<PagedResponse<GradeSubCriteria>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<GradeSubCriteria> response = gradePolicyService.getAllGradeSubCriterias(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GradeSubCriteria> create(@Valid @RequestBody GradeSubCriteriaRequest gradeSubCriteriaRequest,
                                                 @CurrentUser UserPrincipal currentUser) {
        GradeSubCriteria gradeSubCriteria = gradePolicyService.addGradeSubCriteria(gradeSubCriteriaRequest, currentUser);
        return new ResponseEntity<>(gradeSubCriteria, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeSubCriteria> get(@PathVariable(name = "id") Integer id, @CurrentUser UserPrincipal currentUser) {
        GradeSubCriteria gradeSubCriteria = gradePolicyService.getGradeSubCriteria(id, currentUser);
        return new ResponseEntity<>(gradeSubCriteria, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeSubCriteria> update(@PathVariable(name = "id") Integer id,
                                                 @Valid @RequestBody GradeSubCriteriaRequest gradeSubCriteriaRequest, @CurrentUser UserPrincipal currentUser) {
        GradeSubCriteria gradeSubCriteria = gradePolicyService.updateGradeSubCriteria(id, gradeSubCriteriaRequest, currentUser);
        return new ResponseEntity<>(gradeSubCriteria, HttpStatus.OK);
    }

}
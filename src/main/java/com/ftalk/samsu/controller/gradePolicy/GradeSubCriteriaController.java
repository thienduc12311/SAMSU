package com.ftalk.samsu.controller.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeSubCriteriaRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeSubCriteriaResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketResponse;
import com.ftalk.samsu.payload.gradePolicy.PolicyDocumentRequest;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GradePolicyService;
import com.ftalk.samsu.service.GradeTicketService;
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
    @Autowired
    private GradeTicketService gradeTicketService;

    @GetMapping
    public ResponseEntity<PagedResponse<GradeSubCriteriaResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<GradeSubCriteriaResponse> response = gradePolicyService.getAllGradeSubCriterias(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GradeSubCriteriaResponse> create(@Valid @RequestBody GradeSubCriteriaRequest gradeSubCriteriaRequest,
                                                 @CurrentUser UserPrincipal currentUser) {
        GradeSubCriteria gradeSubCriteria = gradePolicyService.addGradeSubCriteria(gradeSubCriteriaRequest, currentUser);
        return new ResponseEntity<>(new GradeSubCriteriaResponse(gradeSubCriteria), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeSubCriteriaResponse> get(@PathVariable(name = "id") Integer id, @CurrentUser UserPrincipal currentUser) {
        GradeSubCriteria gradeSubCriteria = gradePolicyService.getGradeSubCriteria(id, currentUser);
        return new ResponseEntity<>(new GradeSubCriteriaResponse(gradeSubCriteria), HttpStatus.OK);
    }
    @GetMapping("/{id}/gradeTickets")
    public ResponseEntity<PagedResponse<GradeTicketResponse>> getGradeTickets(@PathVariable(name = "id") Integer id,
                                                                              @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                              @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<GradeTicketResponse> response = gradeTicketService.getGradeTicketsByGradeSubCriteriaId(page, size, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<GradeSubCriteriaResponse> update(@PathVariable(name = "id") Integer id,
                                                 @Valid @RequestBody GradeSubCriteriaRequest gradeSubCriteriaRequest, @CurrentUser UserPrincipal currentUser) {
        GradeSubCriteria gradeSubCriteria = gradePolicyService.updateGradeSubCriteria(id, gradeSubCriteriaRequest, currentUser);
        return new ResponseEntity<>(new GradeSubCriteriaResponse(gradeSubCriteria), HttpStatus.OK);
    }

}

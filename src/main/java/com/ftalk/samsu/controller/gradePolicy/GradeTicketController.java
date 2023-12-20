package com.ftalk.samsu.controller.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeSubCriteriaResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketCreateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketUpdateRequest;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GradeTicketService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gradeTicket")
@PreAuthorize("isAuthenticated()")
public class GradeTicketController {
    @Autowired
    private GradeTicketService gradeTicketService;

    @GetMapping
    public ResponseEntity<PagedResponse<GradeTicketResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @CurrentUser UserPrincipal currentUser) {
        PagedResponse<GradeTicketResponse> response = gradeTicketService.getAllGradeTickets(page, size,currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeTicketResponse> get(@PathVariable(name = "id") Integer id, @CurrentUser UserPrincipal currentUser) {
        GradeTicketResponse gradeTicket = gradeTicketService.getGradeTicket(id);
        return new ResponseEntity<>(gradeTicket, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GradeTicketResponse> create(@RequestBody GradeTicketCreateRequest gradeTicketCreateRequest,
                                                  @CurrentUser UserPrincipal currentUser) {
        GradeTicketResponse gradeTicket = gradeTicketService.addGradeTicket(gradeTicketCreateRequest, currentUser);
        return new ResponseEntity<>(gradeTicket, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeTicketResponse> update(@PathVariable(name = "id") Integer id,
                                                      @RequestBody GradeTicketUpdateRequest gradeTicketUpdateRequest, @CurrentUser UserPrincipal currentUser) {
        GradeTicketResponse gradeTicket = gradeTicketService.updateGradeTicketV2(id, gradeTicketUpdateRequest, currentUser);
        return new ResponseEntity<>(gradeTicket, HttpStatus.OK);
    }

}

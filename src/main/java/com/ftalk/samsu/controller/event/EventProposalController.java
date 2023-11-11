package com.ftalk.samsu.controller.event;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.EventProposalEvaluateRequest;
import com.ftalk.samsu.payload.event.EventProposalRequest;
import com.ftalk.samsu.payload.event.EventProposalResponse;
import com.ftalk.samsu.payload.event.EventProposalUpdateRequest;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventProposalService;
import com.ftalk.samsu.utils.AppConstants;
import com.ftalk.samsu.utils.event.EventProposalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/event/proposals")
public class EventProposalController {

    @Autowired
    EventProposalService eventProposalService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<EventProposal>> getAllEventProposal(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<EventProposal> response = eventProposalService.getAllEventProposals(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<PagedResponse<EventProposal>> getMyEventProposal(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @CurrentUser UserPrincipal currentUser) {
        PagedResponse<EventProposal> response = eventProposalService.getAllMyEventProposals(page, size, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{rollnumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<EventProposal>> getEventProposalByCreator(
            @PathVariable(value = "rollnumber") String rollnumber,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @CurrentUser UserPrincipal currentUser) {
        PagedResponse<EventProposal> response = eventProposalService.getEventProposalsByCreatedBy(rollnumber, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<EventProposalResponse> createEventProposal(@Valid @RequestBody EventProposalRequest eventProposalRequest,
                                                             @CurrentUser UserPrincipal currentUser) {
        EventProposal response = eventProposalService.addEventProposal(eventProposalRequest, currentUser);
        return new ResponseEntity<>(new EventProposalResponse(response), HttpStatus.OK);
    }

    @GetMapping("/{eventProposalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<EventProposalResponse> getEventProposal(@PathVariable(value = "eventProposalId") Integer eventProposalId,
                                                          @CurrentUser UserPrincipal currentUser) {
        EventProposal response = eventProposalService.getEventProposal(eventProposalId, currentUser);
        return new ResponseEntity<>(new EventProposalResponse(response), HttpStatus.OK);
    }

    @PutMapping("/{eventProposalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<EventProposalResponse> updateEventProposal(@Valid @RequestBody EventProposalUpdateRequest eventProposalUpdateRequest,
                                                             @PathVariable(value = "eventProposalId") Integer eventProposalId,
                                                             @CurrentUser UserPrincipal currentUser) {
        EventProposal response = eventProposalService.updateEventProposal(eventProposalId, eventProposalUpdateRequest, currentUser);
        return new ResponseEntity<>(new EventProposalResponse(response), HttpStatus.OK);
    }

    @DeleteMapping("/{eventProposalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> deleteEventProposal(@PathVariable(value = "eventProposalId") Integer eventProposalId,
                                                           @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = eventProposalService.deleteEventProposal(eventProposalId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/evaluate/{eventProposalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateEventProposalEvaluate(
            @Valid @RequestBody EventProposalEvaluateRequest eventProposalEvaluateRequest,
            @PathVariable(value = "eventProposalId") Integer eventProposalId,
            @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = eventProposalService.updateEventProposalEvaluate(eventProposalId, eventProposalEvaluateRequest, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}

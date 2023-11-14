package com.ftalk.samsu.controller.event;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.*;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventProposalService;
import com.ftalk.samsu.service.EventService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<Event>> getAllEvent(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<Event> response = eventService.getAllEvents(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/me")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    public ResponseEntity<PagedResponse<EventProposalResponse>> getMyEventProposal(
//            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
//            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
//            @CurrentUser UserPrincipal currentUser) {
//        PagedResponse<EventProposalResponse> response = eventProposalService.getAllMyEventProposals(page, size, currentUser);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping("/user/{rollnumber}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<PagedResponse<EventProposalResponse>> getEventProposalByCreator(
//            @PathVariable(value = "rollnumber") String rollnumber,
//            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
//            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
//            @CurrentUser UserPrincipal currentUser) {
//        PagedResponse<EventProposalResponse> response = eventProposalService.getEventProposalsByCreatedBy(rollnumber, page, size);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest eventCreateRequest,
                                                             @CurrentUser UserPrincipal currentUser) {
        Event response = eventService.addEvent(eventCreateRequest, currentUser);
        return new ResponseEntity<>(new EventResponse(response), HttpStatus.OK);
    }

    @GetMapping("/{eventProposalId}")
    public ResponseEntity<EventResponse> getEventProposal(@PathVariable(value = "eventProposalId") Integer eventProposalId,
                                                          @CurrentUser UserPrincipal currentUser) {
        Event response = eventService.getEvent(eventProposalId, currentUser);
        return new ResponseEntity<>(new EventResponse(response), HttpStatus.OK);
    }

    @PutMapping("/{eventProposalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<EventResponse> updateEventProposal(@Valid @RequestBody EventCreateRequest eventCreateRequest,
                                                             @PathVariable(value = "eventProposalId") Integer eventProposalId,
                                                             @CurrentUser UserPrincipal currentUser) {
        Event response = eventService.updateEvent(eventProposalId, eventCreateRequest, currentUser);
        return new ResponseEntity<>(new EventResponse(response), HttpStatus.OK);
    }

//    @DeleteMapping("/{eventProposalId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
//    public ResponseEntity<ApiResponse> deleteEventProposal(@PathVariable(value = "eventProposalId") Integer eventProposalId,
//                                                           @CurrentUser UserPrincipal currentUser) {
//        ApiResponse apiResponse = eventProposalService.deleteEventProposal(eventProposalId, currentUser);
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }

//    @PutMapping("/evaluate/{eventProposalId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse> updateEventProposalEvaluate(
//            @Valid @RequestBody EventProposalEvaluateRequest eventProposalEvaluateRequest,
//            @PathVariable(value = "eventProposalId") Integer eventProposalId,
//            @CurrentUser UserPrincipal currentUser) {
//        ApiResponse apiResponse = eventProposalService.updateEventProposalEvaluate(eventProposalId, eventProposalEvaluateRequest, currentUser);
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }

}

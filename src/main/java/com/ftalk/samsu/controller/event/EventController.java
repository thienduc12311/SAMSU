package com.ftalk.samsu.controller.event;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.*;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventProposalService;
import com.ftalk.samsu.service.EventService;
import com.ftalk.samsu.service.MailSenderService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    EventService eventService;

    @Autowired
    MailSenderService mailSenderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<EventAllResponse>> getAllEvent(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
//        eventService.evictAllEntries();
        PagedResponse<EventAllResponse> response = eventService.getAllEvents(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<PagedResponse<EventResponse>> getAllEventPublic(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<EventResponse> response = eventService.getAllEventsPublic(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventProposalId}/posts")
    public ResponseEntity<List<Post>> getEventProposalPosts(@PathVariable(value = "eventProposalId") Integer eventProposalId,
                                                            @CurrentUser UserPrincipal currentUser) {
        List<Post> response = eventService.getEventPost(eventProposalId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getEventParticipants(@PathVariable(value = "eventId") Integer eventId) {
        List<ParticipantResponse> response = eventService.getAllEventParticipants(eventId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{eventId}/register")
    public ResponseEntity<ApiResponse> register(@PathVariable(value = "eventId") Integer eventId,
                                                @CurrentUser UserPrincipal currentUser) {
        ApiResponse response = eventService.register(true, eventId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/unregister")
    public ResponseEntity<ApiResponse> unregister(@PathVariable(value = "eventId") Integer eventId,
                                                  @CurrentUser UserPrincipal currentUser) {
        ApiResponse response = eventService.register(false, eventId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("user/{rollnumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<EventResponse>> getEventByRollNumber(
            @PathVariable(value = "rollnumber") String rollnumber,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<EventResponse> response = eventService.getEventsByRollNumber(rollnumber, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<EventResponse>> getMyEvents(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @CurrentUser UserPrincipal currentUser) {
        PagedResponse<EventResponse> response = eventService.getEventsByRollNumber(currentUser.getRollnumber(), page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest eventCreateRequest,
                                                     @CurrentUser UserPrincipal currentUser) {
        Event response = eventService.addEvent(eventCreateRequest, currentUser);
        return new ResponseEntity<>(new EventResponse(response), HttpStatus.OK);
    }

    @PostMapping("/event/{eventId}/checkin/{rollnumber}")
    public ResponseEntity<ApiResponse> checkin(
            @PathVariable(value = "eventId") Integer eventId,
            @PathVariable(value = "rollnumber") String rollnumber,
            @CurrentUser UserPrincipal currentUser) {
        ApiResponse response = eventService.checkIn(eventId, rollnumber, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventProposalId}")
    public ResponseEntity<EventResponse> getEventProposal(@PathVariable(value = "eventProposalId") Integer eventProposalId,
                                                          @CurrentUser UserPrincipal currentUser) {
//        eventService.evictAllEntries();
        EventResponse response = eventService.getEventResponse(eventProposalId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/isFeedback")
    public ResponseEntity<Boolean> isFeedback(@PathVariable(value = "eventId") Integer eventId,
                                              @CurrentUser UserPrincipal currentUser) {
        Boolean response = eventService.isFeedback(eventId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{eventId}/isCheckedIn")
    public ResponseEntity<Boolean> isCheckedIn(@PathVariable(value = "eventId") Integer eventId,
                                              @CurrentUser UserPrincipal currentUser) {
        Boolean response = eventService.isCheckedIn(eventId, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{eventProposalId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<EventResponse> updateEventProposal(@Valid @RequestBody EventCreateRequest eventCreateRequest,
                                                             @PathVariable(value = "eventProposalId") Integer eventProposalId,
                                                             @CurrentUser UserPrincipal currentUser) {
        EventResponse response = eventService.updateEvent(eventProposalId, eventCreateRequest, currentUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

package com.ftalk.samsu.service;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PhotoRequest;
import com.ftalk.samsu.payload.PhotoResponse;
import com.ftalk.samsu.payload.event.EventAllResponse;
import com.ftalk.samsu.payload.event.EventCreateRequest;
import com.ftalk.samsu.payload.event.EventResponse;
import com.ftalk.samsu.payload.event.ParticipantResponse;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;

public interface EventService {

	void evictAllEntries();
	PagedResponse<EventAllResponse> getAllEvents(int page, int size, UserPrincipal currentUser);

	PagedResponse<EventResponse> getAllEventsPublic(int page, int size);

	PagedResponse<EventResponse> getEventsByRollNumber(String rollNumber, int page, int size);
	PagedResponse<EventResponse> getEventBySemester(String semester, int page, int size);

	List<Event> getEventBySemester(String semester);

	Event getEvent(Integer id, UserPrincipal currentUser);

	Boolean isFeedback(Integer eventId, UserPrincipal currentUser);
	Boolean isCheckedIn(Integer eventId, UserPrincipal currentUser);

	EventResponse getEventResponse(Integer id, UserPrincipal currentUser);
	List<ParticipantResponse> getAllEventParticipants(Integer eventId);

	ApiResponse register(boolean isAdd, Integer id, UserPrincipal currentUser);

	ApiResponse checkIn(Integer eventId, String rollnumber, UserPrincipal currentUser);


	List<Post> getEventPost(Integer id, UserPrincipal currentUser);

	EventResponse updateEvent(Integer id, EventCreateRequest eventCreateRequest, UserPrincipal currentUser);

	Event addEvent(EventCreateRequest eventCreateRequest, UserPrincipal currentUser);



}
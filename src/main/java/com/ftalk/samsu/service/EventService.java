package com.ftalk.samsu.service;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PhotoRequest;
import com.ftalk.samsu.payload.PhotoResponse;
import com.ftalk.samsu.payload.event.EventCreateRequest;
import com.ftalk.samsu.payload.event.EventResponse;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;

public interface EventService {

	PagedResponse<EventResponse> getAllEvents(int page, int size);

	PagedResponse<EventResponse> getAllEventsPublic(int page, int size);

	PagedResponse<EventResponse> getEventsByRollNumber(String rollNumber, int page, int size);

	Event getEvent(Integer id, UserPrincipal currentUser);

	ApiResponse register(boolean isAdd, Integer id, UserPrincipal currentUser);

	List<Post> getEventPost(Integer id, UserPrincipal currentUser);

	Event updateEvent(Integer id, EventCreateRequest eventCreateRequest, UserPrincipal currentUser);

	Event addEvent(EventCreateRequest eventCreateRequest, UserPrincipal currentUser);



}
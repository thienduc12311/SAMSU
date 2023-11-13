package com.ftalk.samsu.service;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PhotoRequest;
import com.ftalk.samsu.payload.PhotoResponse;
import com.ftalk.samsu.payload.event.EventCreateRequest;
import com.ftalk.samsu.security.UserPrincipal;

public interface EventService {

	PagedResponse<Event> getAllEvents(int page, int size);

	Event getEvent(Integer id, UserPrincipal currentUser);

	Event updateEvent(Integer id, EventCreateRequest eventCreateRequest, UserPrincipal currentUser);

	Event addEvent(EventCreateRequest eventCreateRequest, UserPrincipal currentUser);

}
package com.ftalk.samsu.service;

import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.PostResponse;
import com.ftalk.samsu.payload.event.EventProposalEvaluateRequest;
import com.ftalk.samsu.payload.event.EventProposalRequest;
import com.ftalk.samsu.payload.event.EventProposalResponse;
import com.ftalk.samsu.payload.event.EventProposalUpdateRequest;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;

public interface EventProposalService {

	PagedResponse<EventProposalResponse> getAllEventProposals(int page, int size);

	PagedResponse<EventProposalResponse> getAllMyEventProposals(int page, int size, UserPrincipal currentUser);

	List<EventProposalResponse> getMyAvailableEventProposals(UserPrincipal currentUser);

	List<EventProposalResponse> getAllAvailableEventProposals();

    PagedResponse<EventProposalResponse> getEventProposalsByCreatedBy(String rollnumber, int page, int size);

	EventProposal updateEventProposal(Integer id, EventProposalUpdateRequest eventProposalUpdateRequest, UserPrincipal currentUser);

	ApiResponse deleteEventProposal(Integer id, UserPrincipal currentUser);

	ApiResponse updateEventProposalEvaluate(Integer id, EventProposalEvaluateRequest status, UserPrincipal currentUser);


	EventProposal addEventProposal(EventProposalRequest eventProposalRequest, UserPrincipal currentUser);

	EventProposal getEventProposal(Integer id, UserPrincipal currentUser);

}

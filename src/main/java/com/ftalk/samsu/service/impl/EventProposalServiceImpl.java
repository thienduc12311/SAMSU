package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostResponse;
import com.ftalk.samsu.payload.event.EventProposalEvaluateRequest;
import com.ftalk.samsu.payload.event.EventProposalRequest;
import com.ftalk.samsu.payload.event.EventProposalUpdateRequest;
import com.ftalk.samsu.repository.EventProposalRepository;
import com.ftalk.samsu.repository.SemesterRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventProposalService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.event.EventProposalConstants;
import com.ftalk.samsu.utils.event.EventUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.ftalk.samsu.utils.AppConstants.*;

@Service
public class EventProposalServiceImpl implements EventProposalService {
    @Autowired
    private EventProposalRepository eventProposalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public PagedResponse<EventProposal> getAllEventProposals(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<EventProposal> eventProposals = eventProposalRepository.findAll(pageable);

        List<EventProposal> content = eventProposals.getNumberOfElements() == 0 ? Collections.emptyList() : eventProposals.getContent();

        return new PagedResponse<>(content, eventProposals.getNumber(), eventProposals.getSize(), eventProposals.getTotalElements(),
                eventProposals.getTotalPages(), eventProposals.isLast());
    }

    @Override
    public PagedResponse<EventProposal> getAllMyEventProposals(int page, int size, UserPrincipal currentUser) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<EventProposal> eventProposals = eventProposalRepository.findAllByCreatorUserId(currentUser.getId(), pageable);

        List<EventProposal> content = eventProposals.getNumberOfElements() == 0 ? Collections.emptyList() : eventProposals.getContent();

        return new PagedResponse<>(content, eventProposals.getNumber(), eventProposals.getSize(), eventProposals.getTotalElements(),
                eventProposals.getTotalPages(), eventProposals.isLast());
    }

    @Override
    public PagedResponse<EventProposal> getEventProposalsByCreatedBy(String rollnumber, int page, int size) {
        Integer userId = userRepository.getIdByRollnumber(rollnumber);

        AppUtils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        eventProposalRepository.findAllByCreatorUserId(userId, pageable);

        return null;
    }

    @Override
    public EventProposal updateEventProposal(Integer id, EventProposalUpdateRequest newEventProposalUpdateRequest, UserPrincipal currentUser) {
        if (!EventUtils.validateFileUrlsS3(newEventProposalUpdateRequest.getFileUrls())) {
            throw new BadRequestException("Invalid urls attached");
        }
        EventProposal eventProposal = eventProposalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EVENT_PROPOSAL, ID, id));
        if (eventProposal.getCreatorUserId().getId().equals(currentUser.getId())) {
            if (EventProposalConstants.ACCEPTED.getValue() == eventProposal.getStatus()) {
                eventProposal.setStatus(EventProposalConstants.WAITING_APPROVE.getValue());
                eventProposal.setAccepterUserId(null);
            }
            eventProposal.setTitle(newEventProposalUpdateRequest.getTitle());
            eventProposal.setContent(newEventProposalUpdateRequest.getContent());
            eventProposal.setModifyAt(new Date());
            eventProposal.setFileUrls(newEventProposalUpdateRequest.getFileUrls());
            return eventProposalRepository.save(eventProposal);
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to edit this eventProposal");
        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public ApiResponse deleteEventProposal(Integer id, UserPrincipal currentUser) {
        EventProposal eventProposal = eventProposalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EVENT_PROPOSAL, ID, id));
        if (eventProposal.getCreatorUserId().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            eventProposalRepository.deleteById(id);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted eventProposal");
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this eventProposal");
        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public ApiResponse updateEventProposalEvaluate(Integer id, EventProposalEvaluateRequest eventProposalEvaluateRequest, UserPrincipal currentUser) {
        EventProposal eventProposal = eventProposalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EVENT_PROPOSAL, ID, id));
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            eventProposal.setStatus(eventProposalEvaluateRequest.getStatus());
            if (!StringUtils.isEmpty(eventProposalEvaluateRequest.getFeedback())) {
                eventProposal.setFeedback(eventProposalEvaluateRequest.getFeedback());
            }
            eventProposalRepository.save(eventProposal);
            return new ApiResponse(Boolean.TRUE, "You successfully updated eventProposal");
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update this eventProposal");
        throw new UnauthorizedException(apiResponse);
    }


    @Override
    public EventProposal addEventProposal(EventProposalRequest eventProposalRequest, UserPrincipal currentUser) {
        if (!EventUtils.validateFileUrlsS3(eventProposalRequest.getFileUrls())) {
            throw new BadRequestException("Invalid urls attached");
        }
        User creator = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER, USERNAME, currentUser.getUsername()));
        EventProposal eventProposal = new EventProposal(eventProposalRequest, EventProposalConstants.WAITING_APPROVE.getValue(), creator);
        return eventProposalRepository.save(eventProposal);
    }

    @Override
    public EventProposal getEventProposal(Integer id, UserPrincipal currentUser) {
        EventProposal eventProposal = eventProposalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EVENT_PROPOSAL, ID, id));
        if (eventProposal.getCreatorUserId().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            return eventProposal;
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to access this eventProposal");
        throw new UnauthorizedException(apiResponse);
    }

}

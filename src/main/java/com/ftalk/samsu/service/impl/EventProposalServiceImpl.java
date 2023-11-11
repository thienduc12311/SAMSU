package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.SamsuApiException;
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
import com.ftalk.samsu.payload.event.EventProposalResponse;
import com.ftalk.samsu.payload.event.EventProposalUpdateRequest;
import com.ftalk.samsu.repository.EventProposalRepository;
import com.ftalk.samsu.repository.SemesterRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.JwtAuthenticationEntryPoint;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventProposalService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.event.EventProposalConstants;
import com.ftalk.samsu.utils.event.EventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(EventProposalServiceImpl.class);
    @Autowired
    private EventProposalRepository eventProposalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public PagedResponse<EventProposalResponse> getAllEventProposals(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<EventProposal> eventProposals = eventProposalRepository.findAll(pageable);

        List<EventProposal> content = eventProposals.getNumberOfElements() == 0 ? Collections.emptyList() : eventProposals.getContent();

        return new PagedResponse<>(EventUtils.listToList(content), eventProposals.getNumber(), eventProposals.getSize(), eventProposals.getTotalElements(),
                eventProposals.getTotalPages(), eventProposals.isLast());
    }

    @Override
    public PagedResponse<EventProposalResponse> getAllMyEventProposals(int page, int size, UserPrincipal currentUser) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        User user = userRepository.getUser(currentUser);

        Page<EventProposal> eventProposals = eventProposalRepository.findAllByCreatorUserId(user, pageable);

        List<EventProposal> content = eventProposals.getNumberOfElements() == 0 ? Collections.emptyList() : eventProposals.getContent();

        return new PagedResponse<>(EventUtils.listToList(content), eventProposals.getNumber(), eventProposals.getSize(), eventProposals.getTotalElements(),
                eventProposals.getTotalPages(), eventProposals.isLast());
    }

    @Override
    public PagedResponse<EventProposalResponse> getEventProposalsByCreatedBy(String rollnumber, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        User user = userRepository.getUserByRollnumber(rollnumber);
        Page<EventProposal> eventProposals = eventProposalRepository.findAllByCreatorUserId(user, pageable);
        List<EventProposal> content = eventProposals.getNumberOfElements() == 0 ? Collections.emptyList() : eventProposals.getContent();
        return new PagedResponse<>(EventUtils.listToList(content), eventProposals.getNumber(), eventProposals.getSize(), eventProposals.getTotalElements(),
                eventProposals.getTotalPages(), eventProposals.isLast());
    }

    @Override
    public EventProposal updateEventProposal(Integer id, EventProposalUpdateRequest newEventProposalUpdateRequest, UserPrincipal currentUser) {
        if (!EventUtils.validateFileUrlsS3(newEventProposalUpdateRequest.getFileUrls())) {
            throw new BadRequestException("Invalid urls attached");
        }
        EventProposal eventProposal = eventProposalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EVENT_PROPOSAL, ID, id));
        boolean isAdmin = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));

        if (isAdmin) {
            updateEvaluate(id, new EventProposalEvaluateRequest(
                    newEventProposalUpdateRequest.getFeedback(), newEventProposalUpdateRequest.getStatus()), currentUser);
        } else {
            eventProposal.setStatus(EventProposalConstants.PROCESSING.getValue());
            eventProposal.setAccepterUserId(null);
        }

        if (eventProposal.getCreatorUserId().getId().equals(currentUser.getId()) || isAdmin) {
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
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            if (updateEvaluate(id,eventProposalEvaluateRequest,currentUser)){
                return new ApiResponse(Boolean.TRUE, "You successfully updated eventProposal");
            } else {
                throw new SamsuApiException(HttpStatus.INTERNAL_SERVER_ERROR,"Update event proposal evaluate failed");
            }
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update this eventProposal");
        throw new UnauthorizedException(apiResponse);
    }

    public boolean updateEvaluate(Integer id, EventProposalEvaluateRequest eventProposalEvaluateRequest, UserPrincipal currentUser) {
        try {
            EventProposal eventProposal = eventProposalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EVENT_PROPOSAL, ID, id));
            eventProposal.setStatus(EventProposalConstants.findValue(eventProposalEvaluateRequest.getStatus()));
            if (EventProposalConstants.APPROVED.getValue() == eventProposal.getStatus()) {
                eventProposal.setAccepterUserId(new User(currentUser.getId()));
            }
            if (!StringUtils.isEmpty(eventProposalEvaluateRequest.getFeedback())) {
                eventProposal.setFeedback(eventProposalEvaluateRequest.getFeedback());
            }
            eventProposalRepository.save(eventProposal);
            return true;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(),ex);
        }
        return false;
    }


    @Override
    public EventProposal addEventProposal(EventProposalRequest eventProposalRequest, UserPrincipal currentUser) {
        if (!EventUtils.validateFileUrlsS3(eventProposalRequest.getFileUrls())) {
            throw new BadRequestException("Invalid urls attached");
        }
        User creator = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER, USERNAME, currentUser.getUsername()));
        EventProposal eventProposal = new EventProposal(eventProposalRequest, EventProposalConstants.PROCESSING.getValue(), creator, true);
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

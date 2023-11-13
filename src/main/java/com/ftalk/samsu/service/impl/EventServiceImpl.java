package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.Album;
import com.ftalk.samsu.model.Photo;
import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PhotoRequest;
import com.ftalk.samsu.payload.PhotoResponse;
import com.ftalk.samsu.payload.event.EventCreateRequest;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionRequest;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventService;
import com.ftalk.samsu.service.PhotoService;
import com.ftalk.samsu.utils.AppConstants;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.event.EventProposalConstants;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ftalk.samsu.utils.AppConstants.*;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EventProposalRepository eventProposalRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public PagedResponse<Event> getAllEvents(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Event> events = eventRepository.findAll(pageable);

		if (events.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(),
                    events.getTotalElements(), events.getTotalPages(), events.isLast());
		}
        return new PagedResponse<>(events.getContent(), events.getNumber(), events.getSize(), events.getTotalElements(),
                events.getTotalPages(), events.isLast());

    }

    @Override
    public Event getEvent(Integer id, UserPrincipal currentUser) {
        return eventRepository.findById(id).orElseThrow(()-> new BadRequestException("EventId not found!!"));
    }

    @Override
    public Event updateEvent(Integer id, EventCreateRequest eventCreateRequest, UserPrincipal currentUser) {
        Event event =  eventRepository.findById(id).orElseThrow(()-> new BadRequestException("EventId not found!!"));
        event.setTitle(eventCreateRequest.getTitle());
        event.setContent(eventCreateRequest.getContent());
        event.setDuration(event.getDuration());
        return eventRepository.save(event);
    }

    @Override
    public Event addEvent(EventCreateRequest eventCreateRequest, UserPrincipal currentUser) {
        User creator = userRepository.getUser(currentUser);
        List<Department> departments = new ArrayList<>(eventCreateRequest.getDepartmentIds().size());
        for (Integer departmentId : eventCreateRequest.getDepartmentIds()) {
            Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new BadRequestException("Departments collaborator not found!!"));
            departments.add(department);
        }
        EventProposal eventProposal = eventProposalRepository.findById(eventCreateRequest.getEventProposalId())
                .orElseThrow(() -> new BadRequestException("EventProposal not found!!"));
        if (eventProposal.getStatus() != EventProposalConstants.APPROVED.getValue()) {
            throw new BadRequestException("EventProposal not approved");
        }
        User eventLeaderUser = userRepository.getUserByRollnumber(eventCreateRequest.getEventLeaderRollnumber());
        Semester semester = semesterRepository.findByName(eventCreateRequest.getSemester())
                .orElseThrow(() -> new BadRequestException("Semester not found!!"));
        List<FeedbackQuestion> feedbackQuestions = getFeedbackQuestions(eventCreateRequest);
        Event event = new Event(eventCreateRequest.getStatus(), eventCreateRequest.getDuration(), eventCreateRequest.getTitle(),
                eventCreateRequest.getContent(), creator, eventProposal, eventLeaderUser, semester, eventCreateRequest.getBannerUrl(),
                eventProposal.getFileUrls(), eventCreateRequest.getStartTime());
        event.setDepartments(departments);
        event.setFeedbackQuestions(feedbackQuestions);
        return eventRepository.save(event);
    }

    @NotNull
    private List<FeedbackQuestion> getFeedbackQuestions(EventCreateRequest eventCreateRequest) {
        List<FeedbackQuestion> feedbackQuestions = new ArrayList<>(eventCreateRequest.getFeedbackQuestionRequestList().size());
        for (FeedbackQuestionRequest feedbackQuestionRequest : eventCreateRequest.getFeedbackQuestionRequestList()) {
            FeedbackQuestion feedbackQuestion = new FeedbackQuestion(feedbackQuestionRequest.getType(),
                    feedbackQuestionRequest.getQuestion(), feedbackQuestionRequest.getAnswer());
            feedbackQuestions.add(feedbackQuestion);
        }
        return feedbackQuestions;
    }

}

package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.event.NotificationEvent;
import com.ftalk.samsu.event.TaskAssignmentEvent;
import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.Album;
import com.ftalk.samsu.model.Photo;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.model.event.*;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.model.participant.ParticipantId;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PhotoRequest;
import com.ftalk.samsu.payload.PhotoResponse;
import com.ftalk.samsu.payload.event.*;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionRequest;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.*;
import com.ftalk.samsu.utils.AppConstants;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import com.ftalk.samsu.utils.event.EventConstants;
import com.ftalk.samsu.utils.event.EventProcessingConstants;
import com.ftalk.samsu.utils.event.EventProposalConstants;
import com.ftalk.samsu.utils.notification.NotificationConstant;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private FeedbackQuestionRepository feedbackQuestionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    AssigneeRepository assigneeRepository;

    @Autowired
    GradePolicyService gradePolicyService;

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    ApplicationEventMulticaster eventPublisher;
    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    @Caching(evict = {
            @CacheEvict(value = {"eventCache"}, allEntries = true),
            @CacheEvict(value = {"eventsCache"}, allEntries = true)
    })
    public void evictAllEntries() {
    }

    @Override
    public PagedResponse<EventAllResponse> getAllEvents(int page, int size, UserPrincipal currentUser) {
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            return getAllAdminEvents(page, size);
        } else {
            return getAllManagerEvents(page, size, currentUser);
        }
    }

    @Cacheable(value = "eventsCache", key = "#page + '_' + #size")
    public PagedResponse<EventAllResponse> getAllAdminEvents(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Event> events = eventRepository.findAll(pageable);
        if (events.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(events.getContent(), EventAllResponse::new), events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast());
    }

    @Cacheable(value = "eventsManagerCache", key = "#page + '_' + #size")
    public PagedResponse<EventAllResponse> getAllManagerEvents(int page, int size, UserPrincipal currentUser) {
        AppUtils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Event> events = eventRepository.findAllByCreatorUserId(currentUser.getId(), pageable);
        if (events.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(events.getContent(), EventAllResponse::new), events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast());
    }


    @Override
    public PagedResponse<EventResponse> getAllEventsPublic(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Event> events = eventRepository.findByStatus(EventConstants.PUBLIC.getValue(), pageable);
        return getEventPagedResponse(events);
    }

    @Override
    public PagedResponse<EventResponse> getEventsByRollNumber(String rollNumber, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        User user = userRepository.getUserByRollnumber(rollNumber);
        if (user == null) throw new BadRequestException("User not found!!");
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Event> events = eventRepository.findByParticipantsRollnumber(rollNumber, pageable);
        return getEventPagedResponse(events);
    }

    @Override
    public PagedResponse<EventResponse> getEventBySemester(String semester, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);
        Page<Event> events = eventRepository.findBySemesterName(semester, pageable);
        return getEventPagedResponse(events);
    }

    @Override
    public List<Event> getEventBySemester(String semester) {
        return eventRepository.findBySemesterName(semester);
    }

    private PagedResponse<EventResponse> getEventPagedResponse(Page<Event> events) {
        if (events.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(events.getContent(), EventResponse::new), events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast());
    }

    @Override
    public Event getEvent(Integer id, UserPrincipal currentUser) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new BadRequestException("EventId not found!!"));
        if (event.getStatus().equals(EventConstants.PUBLIC.getValue())) {
            return event;
        }
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            return event;
        }
        throw new UnauthorizedException("You don't have permission");
    }

    @Override
    public Boolean isFeedback(Integer eventId, UserPrincipal currentUser) {
        Participant participant = participantRepository.findById(new ParticipantId(currentUser.getId(), eventId))
                .orElseThrow(() -> new ResourceNotFoundException("Participant", "ID", eventId + " " + currentUser.getId()));
        return participant.getCheckout() != null;
    }

    @Override
    public Boolean isCheckedIn(Integer eventId, UserPrincipal currentUser) {
        Participant participant = participantRepository.findById(new ParticipantId(currentUser.getId(), eventId))
                .orElseThrow(() -> new ResourceNotFoundException("Participant", "ID", eventId + " " + currentUser.getId()));
        return participant.getCheckin() != null;
    }

    @Override
//    @Cacheable(value = "eventCache", key = "#id")
    public EventResponse getEventResponse(Integer id, UserPrincipal currentUser) {
        return new EventResponse(getEvent(id, currentUser));
    }

    @Override
    public List<ParticipantResponse> getAllEventParticipants(Integer eventId) {
        List<Participant> participants = participantRepository.findByParticipantId_EventsId(eventId);
        List<Integer> ids = participants.parallelStream().map(participant -> participant.getParticipantId().getUsersId()).collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getMapUserById(ids);
        return participants.parallelStream().map(
                        participant -> new ParticipantResponse(participant.getParticipantId().getEventsId(),
                                new UserProfileReduce(userMap.get(participant.getParticipantId().getUsersId())),
                                participant.getCheckin(), participant.getCheckout()))
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse register(boolean isAdd, Integer id, UserPrincipal currentUser) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new BadRequestException("EventId not found!!"));
        User user = userRepository.getUser(currentUser);
        if (user == null) throw new BadRequestException("Your not found!!");
        if (isAdd) {
            if (event.getParticipants() == null) event.setParticipants(new HashSet<>());
            event.getParticipants().add(user);
        } else {
            if (event.getParticipants() != null)
                event.getParticipants().remove(user);
        }
        eventRepository.save(event);
        return new ApiResponse(Boolean.TRUE, "Update success");
    }

    @Override
    public ApiResponse updateProcessing(Short status, Integer id, UserPrincipal currentUser) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new BadRequestException("EventId not found!!"));
        User user = userRepository.getUser(currentUser);
        boolean isEventLeader = event.getEventLeaderUser() != null && Objects.equals(currentUser.getId(), event.getEventLeaderUser().getId());
        if (user == null) throw new BadRequestException("Your not found!!");
        if (isEventLeader || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            event.setProcessStatus(status);
            eventRepository.save(event);
            return new ApiResponse(Boolean.TRUE, "Update success");
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update event status");
        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public String getProcessStatus(Integer id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new BadRequestException("EventId not found!!"));
        return Objects.requireNonNull(EventProcessingConstants.findByValue(event.getProcessStatus())).name();
    }

    @Override
    public ApiResponse checkIn(Integer eventId, String rollnumber, UserPrincipal currentUser) {
        boolean havePermission = taskService.checkPermissionCheckIn(eventId, currentUser.getId());
        User user = userRepository.getUserByRollnumber(rollnumber);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new BadRequestException("EventId not found!!"));
        if (EventProcessingConstants.CHECK_IN.getValue() != event.getProcessStatus()) {
            throw new BadRequestException("Not in check-in time");
        }
        if (havePermission
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            Optional<Participant> participantOptional = participantRepository.findById(new ParticipantId(user.getId(), eventId));
            if (participantOptional.isPresent()) {
                Participant participant = participantOptional.get();
                if (participant.getCheckin() != null) {
                    throw new BadRequestException("This user already checkin!");
                }
                participant.setCheckin(new Date());
                participantRepository.save(participant);
                return new ApiResponse(Boolean.TRUE, "Checkin success");
            }
            return new ApiResponse(Boolean.FALSE, "Checkin failed. User is not registered");
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to get profile student");
        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public List<Post> getEventPost(Integer id, UserPrincipal currentUser) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new BadRequestException("EventId not found!!"));
        return event.getPosts();
    }


    //    @CachePut(value = {"eventCache"}, key = "#id")
    @CacheEvict(value = {"eventsCache", "eventsManagerCache"}, allEntries = true)
    @Override
    public EventResponse updateEvent(Integer id, EventCreateRequest eventCreateRequest, UserPrincipal currentUser) {
        User creator = userRepository.getUser(currentUser);
        GradeSubCriteria gradeSubCriteria = gradePolicyService.getGradeSubCriteria(eventCreateRequest.getSubGradeCriteriaId(), currentUser);
        Event event = eventRepository.findById(id).orElseThrow(() -> new BadRequestException("EventId not found!!"));
        User eventLeaderUser = userRepository.getUserByRollnumber(eventCreateRequest.getEventLeaderRollnumber());
        Set<User> participants = userRepository.findAllByRollnumberIn(eventCreateRequest.getRollnumbers());
        EventProposal eventProposal = eventProposalRepository.findById(eventCreateRequest.getEventProposalId()).orElseThrow(() -> new BadRequestException("EventProposal not found!!"));
        Semester semester = semesterRepository.findByName(eventCreateRequest.getSemester()).orElseThrow(() -> new BadRequestException("Semester not found!!"));
        List<Department> departments = eventCreateRequest.getDepartmentIds() != null ? departmentRepository.findAllById(eventCreateRequest.getDepartmentIds()) : null;
        boolean isPublicStatus = (eventCreateRequest.getStatus() != null) && (eventCreateRequest.getStatus().equals(EventConstants.PUBLIC.getValue())) && (event.getStatus() != EventConstants.PUBLIC.getValue());
        event.setStatus(eventCreateRequest.getStatus());
        event.setAttendGradeSubCriteria(gradeSubCriteria);
        event.setDuration(eventCreateRequest.getDuration());
        event.setTitle(eventCreateRequest.getTitle());
        event.setContent(eventCreateRequest.getContent());
        event.setAttendScore(eventCreateRequest.getAttendScore());
        event.setEventProposal(eventProposal);
        event.setEventLeaderUser(eventLeaderUser);
        event.setSemester(semester);
        event.setBannerUrl(eventCreateRequest.getBannerUrl());
        event.setFileUrls(eventCreateRequest.getFileUrls());
        event.setStartTime(eventCreateRequest.getStartTime());
        event.setParticipants(participants);
        event.setDepartments(departments);
        if (eventCreateRequest.getProcessStatus() != null)
            event.setProcessStatus(eventCreateRequest.getProcessStatus());
//        feedbackQuestionRepository.deleteAllByEventId(event.getId());
//        List<FeedbackQuestion> feedbackQuestions = getFeedbackQuestions(eventCreateRequest, event);
//        feedbackQuestionRepository.saveAll(feedbackQuestions);
//        event.setFeedbackQuestions(feedbackQuestions);
//        if (eventCreateRequest.getTaskRequests() != null) {
//            event.setTasks(getTask(eventCreateRequest, event, creator, currentUser));
//        }
        Event eventSaved = eventRepository.save(event);
        if (isPublicStatus) {
            pushEventNotification(eventSaved);
        }
        return new EventResponse(eventSaved);
    }


    @CacheEvict(value = {"eventCache"}, key = "#eventId")
    public void removeEventCache(Integer eventId) {
    }

    @CacheEvict(value = {"eventsCache", "eventsManagerCache"}, allEntries = true)
    @Override
    @Transactional
    public Event addEvent(EventCreateRequest eventCreateRequest, UserPrincipal currentUser) {
        eventCreateRequest.validate();
        User creator = userRepository.getUser(currentUser);
        List<Department> departments = eventCreateRequest.getDepartmentIds() != null ? departmentRepository.findAllById(eventCreateRequest.getDepartmentIds()) : null;
        EventProposal eventProposal = eventProposalRepository.findById(eventCreateRequest.getEventProposalId()).orElseThrow(() -> new BadRequestException("EventProposal not found!!"));
        GradeSubCriteria gradeSubCriteria = eventCreateRequest.getSubGradeCriteriaId() != null ? gradePolicyService.getGradeSubCriteria(eventCreateRequest.getSubGradeCriteriaId(), currentUser) : null;
        if (eventProposal.getStatus() != EventProposalConstants.APPROVED.getValue()) {
            throw new BadRequestException("EventProposal not approved");
        }
        User eventLeaderUser = userRepository.getUserByRollnumber(eventCreateRequest.getEventLeaderRollnumber());
        Set<User> participants = userRepository.findAllByRollnumberIn(eventCreateRequest.getRollnumbers());
        Semester semester = semesterRepository.findByName(eventCreateRequest.getSemester()).orElseThrow(() -> new BadRequestException("Semester not found!!"));
        Event event = new Event(eventCreateRequest.getStatus(), eventCreateRequest.getDuration(), eventCreateRequest.getTitle(), eventCreateRequest.getContent(), creator, eventCreateRequest.getAttendScore(), eventProposal, eventLeaderUser, semester, eventCreateRequest.getBannerUrl(), eventCreateRequest.getFileUrls(), eventCreateRequest.getStartTime());
        event.setParticipants(participants);
        event.setProcessStatus(EventProcessingConstants.COMING.getValue());
        event.setDepartments(departments);
        event.setAttendGradeSubCriteria(gradeSubCriteria);
        Event eventSaved = eventRepository.save(event);
        List<FeedbackQuestion> feedbackQuestions = getFeedbackQuestions(eventCreateRequest, eventSaved);
        feedbackQuestionRepository.saveAll(feedbackQuestions);
        eventSaved.setFeedbackQuestions(feedbackQuestions);
        if (eventCreateRequest.getTaskRequests() != null) {
            eventSaved.setTasks(getTask(eventCreateRequest, eventSaved, creator, currentUser));
        }
        if (eventSaved.getStatus().equals(EventConstants.PUBLIC.getValue()))
            pushEventNotification(eventSaved, eventCreateRequest);
        return eventSaved;
    }

    private List<FeedbackQuestion> getFeedbackQuestions(EventCreateRequest eventCreateRequest, Event event) {
        List<FeedbackQuestion> feedbackQuestions = new ArrayList<>(eventCreateRequest.getFeedbackQuestionRequestList().size());
        for (FeedbackQuestionRequest feedbackQuestionRequest : eventCreateRequest.getFeedbackQuestionRequestList()) {
            FeedbackQuestion feedbackQuestion = new FeedbackQuestion(feedbackQuestionRequest.getType(), feedbackQuestionRequest.getQuestion(), feedbackQuestionRequest.getAnswer());
            feedbackQuestion.setEvent(event);
            feedbackQuestions.add(feedbackQuestion);
        }
        return feedbackQuestions;
    }

    private List<Task> getTask(EventCreateRequest eventCreateRequest, Event event, User creator, UserPrincipal currentUser) {
        List<Task> tasks = new ArrayList<>(eventCreateRequest.getTaskRequests().size());
        for (TaskRequest taskRequest : eventCreateRequest.getTaskRequests()) {
            GradeSubCriteria gradeSubCriteria = gradePolicyService.getGradeSubCriteria(taskRequest.getGradeSubCriteriaId(), currentUser);
            Task task = new Task(taskRequest);
            task.setEvent(event);
            task.setGradeSubCriteria(gradeSubCriteria);
            task.setCreatorUserId(creator);
            Task taskSaved = taskRepository.save(task);

            Map<String, User> assigneeUser = userService.getMapUserByRollnumber(taskRequest.getAssigneeRollnumber());
            for (AssigneeRequest assigneeRequest : taskRequest.getAssignees()) {
                Assignee assignee = new Assignee(new AssigneeId(taskSaved.getId(), assigneeUser.get(assigneeRequest.getRollnumber()).getId()), assigneeRequest.getStatus());
                assigneeRepository.save(assignee);
            }
            tasks.add(taskSaved);
        }
        return tasks;
    }

    private void pushEventNotification(Event event) {
        List<Task> tasks = event.getTasks();
        if (tasks != null) {
            tasks.forEach(task -> {
                Set<Integer> assigneeIds = new HashSet<>();
                if (task.getAssignees() == null) return;
                task.getAssignees().forEach(assignee -> {
                    assigneeIds.add(assignee.getId().getUsersId());
                });
                taskScheduler.schedule(() -> {
                    eventPublisher.multicastEvent(new NotificationEvent(this, assigneeIds, NotificationConstant.NOTIFICATION_TASK_TITLE, NotificationConstant.genTaskAssignmentNotificationContent(task.getTitle(), event.getTitle(), task.getDeadline()), ""));
                }, new Date(System.currentTimeMillis() + 20 * 1000));
            });
        }

        pushEvent(event);
    }

    private void pushEvent(Event event) {
        long startDateTime = event.getStartTime().getTime() - 7 * 3600 * 1000;
        eventPublisher.multicastEvent(new NotificationEvent(this, null, NotificationConstant.NOTIFICATION_NEW_EVENT_TITLE, NotificationConstant.genEventNotificationContent(event.getTitle()), ""));

        if (startDateTime - 15 * 60 * 1000 <= System.currentTimeMillis()) {
            eventPublisher.multicastEvent(new NotificationEvent(this, event.getParticipants().stream().map(User::getId).collect(Collectors.toSet()), NotificationConstant.NOTIFICATION_EVENT_TITLE, NotificationConstant.genEventNotificationCheckinContent(event.getTitle()), ""));
        } else {
            taskScheduler.schedule(() -> {
                List<Participant> newParticipant = participantRepository.findByParticipantId_EventsId(event.getId());
                eventPublisher.multicastEvent(new NotificationEvent(this, newParticipant.stream().map(participant -> participant.getParticipantId().getUsersId()).collect(Collectors.toSet()), NotificationConstant.NOTIFICATION_EVENT_TITLE, NotificationConstant.genEventNotificationCheckinContent(event.getTitle()), ""));
            }, new Date(startDateTime - 15 * 60 * 1000));
        }
        taskScheduler.schedule(() -> {
            List<Participant> newParticipant = participantRepository.findByParticipantId_EventsId(event.getId());
            eventPublisher.multicastEvent(new NotificationEvent(this, newParticipant.stream().map(participant -> participant.getParticipantId().getUsersId()).collect(Collectors.toSet()), NotificationConstant.NOTIFICATION_EVENT_TITLE, NotificationConstant.genEventNotificationCheckoutContent(event.getTitle()), ""));
        }, new Date(startDateTime + (long) (event.getDuration() - 15) * 60 * 1000));
    }

    private void pushEventNotification(Event event, EventCreateRequest eventCreateRequest) {
        for (TaskRequest taskRequest : eventCreateRequest.getTaskRequests()) {
            Map<String, User> assigneeUser = userService.getMapUserByRollnumber(taskRequest.getAssigneeRollnumber());
            if (assigneeUser != null) {
                Set<Integer> assigneeIds = new HashSet<>();
                assigneeUser.forEach((rollnumber, user) -> {
                    assigneeIds.add(user.getId());
                });
                taskScheduler.schedule(() -> {
                    eventPublisher.multicastEvent(new NotificationEvent(this, assigneeIds, NotificationConstant.NOTIFICATION_TASK_TITLE, NotificationConstant.genTaskAssignmentNotificationContent(taskRequest.getTitle(), event.getTitle(), taskRequest.getDeadline()), ""));
                }, new Date(System.currentTimeMillis() + 20 * 1000));
            }
        }
        pushEvent(event);
    }


}

package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.model.participant.ParticipantId;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeAllEntryResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeAllResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeResponse;
import com.ftalk.samsu.payload.group.GroupImportMemberResponse;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.payload.group.MemberImportFailed;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventService;
import com.ftalk.samsu.service.GradeService;
import com.ftalk.samsu.service.GradeTicketService;
import com.ftalk.samsu.service.GroupService;
import com.ftalk.samsu.utils.event.AssigneeConstants;
import com.ftalk.samsu.utils.event.TaskConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GradeServiceImpl implements GradeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradeServiceImpl.class);
    @Autowired
    private GradeService gradeService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventService eventService;

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private GradeTicketService gradeTicketService;

    @Override
    public List<GradeResponse> getGradeHistory(String rollnumber, String semester, UserPrincipal currentUser) {
        if (currentUser.getRollnumber().equals(rollnumber)
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            User creator = userRepository.getUserByRollnumber(rollnumber);
            List<Event> events = eventService.getEventBySemester(semester);
            Map<Integer, Event> participantEvent = events.parallelStream().collect(Collectors.toMap(Event::getId, Function.identity()));
            List<ParticipantId> participantIds = events.parallelStream().map((e) ->
                    new ParticipantId(creator.getId(), e.getId())).collect(Collectors.toList());
            List<Participant> participants = participantRepository.findAllByParticipantIdIn(participantIds);
            List<GradeResponse> gradeResponses = participants.parallelStream()
                    .filter(participant -> participant.getCheckin() != null && participant.getCheckout() != null)
                    .map(participant -> new GradeResponse(participantEvent.get(participant.getParticipantId().getEventsId()), participant.getCheckout()))
                    .collect(Collectors.toList());

            //task grade
            Map<Integer, Task> tasks = getAllTask(events);
            List<AssigneeId> assigneeIds = tasks.keySet().parallelStream().map(taskId -> new AssigneeId(taskId, creator.getId()))
                    .collect(Collectors.toList());
            List<Assignee> assignees = assigneeRepository.findAllByIdIn(assigneeIds);
            gradeResponses.addAll(assignees.parallelStream()
                    .filter(assignee -> assignee.getStatus() != null
                            && AssigneeConstants.APPROVED.getValue() == assignee.getStatus())
                    .map(assignee -> new GradeResponse(tasks.get(assignee.getId().getTasksId()),
                            assignee.getCreatedAt())).collect(Collectors.toList()));

            //ticket grade
            List<GradeTicket> gradeTickets = gradeTicketService.finAllGradeTicketApproved(semester, creator.getId());
            gradeResponses.addAll(gradeTickets.parallelStream().map(gradeTicket ->
                    new GradeResponse(gradeTicket, gradeTicket.getCreatedAt())).collect(Collectors.toList()));

            gradeResponses.sort((o1, o2) -> o2.getTime().after(o1.getTime()) ? -1 : 1);
            return gradeResponses;
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to get this history");
        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public List<GradeAllEntryResponse> getAllGrade(String semester, UserPrincipal currentUser) {
        GradeAllResponse gradeAllResponse = new GradeAllResponse();
        List<GradeAllEntryResponse> grades = new ArrayList<>();

        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            ConcurrentMap<Integer, GradeAllEntryResponse> students = userRepository.findAllByRoleAndStatus(UserRole.ROLE_STUDENT, (short) 0)
                    .parallelStream().collect(Collectors.toConcurrentMap(User::getId, GradeAllEntryResponse::new));
//            ConcurrentHashMap<Integer,Short>
            List<Event> events = eventService.getEventBySemester(semester);
            events.parallelStream().forEach(event -> {
                List<Task> tasks = event.getTasks();
                if (tasks != null) {
                    tasks.parallelStream().forEach(task -> {
                        if (task.getStatus().equals(TaskConstants.REVIEWED.getValue())) {
                            List<Assignee> assignees = task.getAssignees();
                            if (assignees != null) {
                                assignees.parallelStream().forEach(assignee -> {
                                    if (assignee.getStatus().equals(AssigneeConstants.APPROVED.getValue())) {
                                        students.get(assignee.getId().getUsersId()).addScoreWithSubCriteriaId(task.getGradeSubCriteria().getId(), task.getScore());
                                    }
                                });
                            }
                        }
                    });
                }
                List<Participant> participants = event.getParticipantRaws();
                if (participants != null){
                    participants.parallelStream().forEach(participant -> {
                        if (participant.getCheckin() != null && participant.getCheckout() != null){
                            students.get(participant.getParticipantId().getUsersId()).addScoreWithSubCriteriaId(event.getGradeSubCriteria().getId(), task.getScore());
                        }
                    });
                }
            });

            Map<Integer, Event> participantEvent = events.parallelStream().collect(Collectors.toMap(Event::getId, Function.identity()));
            List<ParticipantId> participantIds = events.parallelStream().map((e) ->
                    new ParticipantId(creator.getId(), e.getId())).collect(Collectors.toList());
            List<Participant> participants = participantRepository.findAllByParticipantIdIn(participantIds);
            List<GradeResponse> gradeResponses = participants.parallelStream()
                    .filter(participant -> participant.getCheckin() != null && participant.getCheckout() != null)
                    .map(participant -> new GradeResponse(participantEvent.get(participant.getParticipantId().getEventsId()), participant.getCheckout()))
                    .collect(Collectors.toList());

            //task grade
            Map<Integer, Task> tasks = getAllTask(events);
            List<AssigneeId> assigneeIds = tasks.keySet().parallelStream().map(taskId -> new AssigneeId(taskId, creator.getId()))
                    .collect(Collectors.toList());
            List<Assignee> assignees = assigneeRepository.findAllByIdIn(assigneeIds);
            gradeResponses.addAll(assignees.parallelStream()
                    .filter(assignee -> assignee.getStatus() != null
                            && AssigneeConstants.APPROVED.getValue() == assignee.getStatus())
                    .map(assignee -> new GradeResponse(tasks.get(assignee.getId().getTasksId()),
                            assignee.getCreatedAt())).collect(Collectors.toList()));

            //ticket grade
            List<GradeTicket> gradeTickets = gradeTicketService.finAllGradeTicketApproved(semester, creator.getId());
            gradeResponses.addAll(gradeTickets.parallelStream().map(gradeTicket ->
                    new GradeResponse(gradeTicket, gradeTicket.getCreatedAt())).collect(Collectors.toList()));

            gradeResponses.sort((o1, o2) -> o2.getTime().after(o1.getTime()) ? -1 : 1);
            return gradeResponses;
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to get this score");
        throw new UnauthorizedException(apiResponse);
    }


    @Transactional
    public Map<Integer, Task> getAllTask(List<Event> events) {
        Map<Integer, Task> taskMap = new HashMap<>();
        for (Event event : events) {
            for (Task task : event.getTasks()) {
                if (task != null) {
                    taskMap.put(task.getId(), task);
                }
            }
        }
        return taskMap;
    }
}

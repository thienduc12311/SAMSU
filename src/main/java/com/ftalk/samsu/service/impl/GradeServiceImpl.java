package com.ftalk.samsu.service.impl;
import org.springframework.security.core.GrantedAuthority;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.model.participant.ParticipantId;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.gradePolicy.*;
import com.ftalk.samsu.payload.group.GroupImportMemberResponse;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.payload.group.MemberImportFailed;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.*;
import com.ftalk.samsu.utils.ListConverter;
import com.ftalk.samsu.utils.event.AssigneeConstants;
import com.ftalk.samsu.utils.event.TaskConstants;
import com.ftalk.samsu.utils.grade.GradeTicketConstants;
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

    @Autowired
    private GradePolicyService gradePolicyService;

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
//    public GradeAllResponse getAllGrade(String semester, UserPrincipal currentUser) {
//        GradeAllResponse gradeAllResponse = new GradeAllResponse();
//
//        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
//                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
//            ConcurrentMap<Integer, GradeAllEntryResponse> students = userRepository.findAllByRoleAndStatus(UserRole.ROLE_STUDENT, (short) 1)
//                    .parallelStream().collect(Collectors.toConcurrentMap(User::getId, GradeAllEntryResponse::new));
////            ConcurrentHashMap<Integer,Short>
//            List<Event> events = eventService.getEventBySemester(semester);
//            if (events != null) {
//                events.forEach(event -> {
//                    List<Task> tasks = event.getTasks();
//                    if (tasks != null) {
//                        tasks.stream()
//                                .filter(task -> TaskConstants.REVIEWED.getValue() == task.getStatus())
//                                .forEach(task -> {
//                                    List<Assignee> assignees = task.getAssignees();
//                                    if (assignees != null) {
//                                        assignees.stream()
//                                                .filter(assignee -> students.containsKey(assignee.getId().getUsersId())
//                                                        && AssigneeConstants.APPROVED.getValue()== assignee.getStatus())
//                                                .forEach(assignee -> {
//                                                    int taskId = task.getId(); // Assuming a method like getId() is available in Task class
//                                                    int userId = assignee.getId().getUsersId();
//                                                    short score = task.getScore();
//                                                    students.get(userId).addScoreWithSubCriteriaId(taskId, score);
//                                                });
//                                    }
//                                });
//                    }
//                    List<Participant> participants = event.getParticipantRaws();
//                    if (participants != null) {
//                        participants.forEach(participant -> {
//                            if (participant.getCheckin() != null && participant.getCheckout() != null) {
//                                if (students.get(participant.getParticipantId().getUsersId()) != null && event.getAttendGradeSubCriteria() != null)
//                                    students.get(participant.getParticipantId().getUsersId()).addScoreWithSubCriteriaId(event.getAttendGradeSubCriteria().getId(), event.getAttendScore());
//                            }
//                        });
//                    }
//                });
//            }
//
//
//            List<GradeTicket> gradeTickets = gradeTicketService.finAllGradeTicketApproved(semester);
//            if (gradeTickets != null) {
//                gradeTickets.forEach(gradeTicket -> {
//                    if (students.get(gradeTicket.getCreatorUser().getId()) != null)
//                        students.get(gradeTicket.getCreatorUser().getId()).addScoreWithSubCriteriaId(gradeTicket.getGradeSubCriteria().getId(), gradeTicket.getScore());
//                });
//            }
//            List<GradeCriteria> gradeCriteriaList = gradePolicyService.getAllGradeCriteria();
//            List<GradeSubCriteria> gradeSubCriteriaList = new ArrayList<>();
//            for (GradeCriteria gradeCriteria : gradeCriteriaList) {
//                if (gradeCriteria.getGradeSubCriteriaList() != null) {
//                    gradeSubCriteriaList.addAll(gradeCriteria.getGradeSubCriteriaList());
//                }
//                ;
//            }
//            gradeAllResponse.setGradeSubCriteriaResponses(ListConverter.listToList(gradeSubCriteriaList, GradeSubCriteriaResponse::new));
//            gradeAllResponse.setGradeCriteriaResponses(ListConverter.listToList(gradeCriteriaList, GradeCriteriaResponse::new));
//            gradeAllResponse.setStudentGrade(new ArrayList<>(students.values()));
//            return gradeAllResponse;
//        }
//        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to get this score");
//        throw new UnauthorizedException(apiResponse);
//    }

    public GradeAllResponse getAllGrade(String semester, UserPrincipal currentUser) {
        GradeAllResponse gradeAllResponse = new GradeAllResponse();

        if (currentUserHasPermission(currentUser)) {
            ConcurrentMap<Integer, GradeAllEntryResponse> students = getStudentResponses();

            processEvents(semester, students);
            processGradeTickets(semester, students);

            List<GradeCriteria> gradeCriteriaList = gradePolicyService.getAllGradeCriteria();
            List<GradeSubCriteria> gradeSubCriteriaList = extractGradeSubCriteriaList(gradeCriteriaList);

            gradeAllResponse.setGradeSubCriteriaResponses(
                    ListConverter.listToList(gradeSubCriteriaList, GradeSubCriteriaResponse::new));
            gradeAllResponse.setGradeCriteriaResponses(
                    ListConverter.listToList(gradeCriteriaList, GradeCriteriaResponse::new));
            gradeAllResponse.setStudentGrade(new ArrayList<>(students.values()));

            return gradeAllResponse;
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to get this score");
        throw new UnauthorizedException(apiResponse);
    }

    private boolean currentUserHasPermission(UserPrincipal currentUser) {
        Set<String> roles = currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return roles.contains(RoleName.ROLE_ADMIN.toString()) || roles.contains(RoleName.ROLE_MANAGER.toString());
    }

    private ConcurrentMap<Integer, GradeAllEntryResponse> getStudentResponses() {
        return userRepository.findAllByRoleAndStatus(UserRole.ROLE_STUDENT, (short) 1)
                .parallelStream().collect(Collectors.toConcurrentMap(User::getId, GradeAllEntryResponse::new));
    }

    private void processEvents(String semester, ConcurrentMap<Integer, GradeAllEntryResponse> students) {
        List<Event> events = eventService.getEventBySemester(semester);
        if (events != null) {
            events.forEach(event -> {
                processEventTasks(event.getTasks(), students);
                processEventParticipants(event.getParticipantRaws(), event, students);
            });
        }
    }

    private void processEventTasks(List<Task> tasks, ConcurrentMap<Integer, GradeAllEntryResponse> students) {
        // ... (Your existing code for processing event tasks)
        if (tasks != null) {
            tasks.stream()
                    .filter(task -> TaskConstants.REVIEWED.getValue() == task.getStatus())
                    .forEach(task -> {
                        List<Assignee> assignees = task.getAssignees();
                        if (assignees != null) {
                            assignees.stream()
                                    .filter(assignee -> students.containsKey(assignee.getId().getUsersId())
                                            && AssigneeConstants.APPROVED.getValue()== assignee.getStatus())
                                    .forEach(assignee -> {
                                        int taskId = task.getId(); // Assuming a method like getId() is available in Task class
                                        int userId = assignee.getId().getUsersId();
                                        short score = task.getScore();
                                        students.get(userId).addScoreWithSubCriteriaId(taskId, score);
                                    });
                        }
                    });
        }
    }

    private void processEventParticipants(List<Participant> participants, Event event,
                                          ConcurrentMap<Integer, GradeAllEntryResponse> students) {
        // ... (Your existing code for processing event participants)
    }

    private void processGradeTickets(String semester, ConcurrentMap<Integer, GradeAllEntryResponse> students) {
        List<GradeTicket> gradeTickets = gradeTicketService.finAllGradeTicketApproved(semester);
        if (gradeTickets != null) {
            gradeTickets.forEach(gradeTicket -> {
                students.computeIfPresent(gradeTicket.getCreatorUser().getId(), (userId, entryResponse) -> {
                    entryResponse.addScoreWithSubCriteriaId(gradeTicket.getGradeSubCriteria().getId(), gradeTicket.getScore());
                    return entryResponse;
                });
            });
        }
    }

    private List<GradeSubCriteria> extractGradeSubCriteriaList(List<GradeCriteria> gradeCriteriaList) {
        return gradeCriteriaList.stream()
                .flatMap(criteria -> Optional.ofNullable(criteria.getGradeSubCriteriaList()).orElseGet(Collections::emptyList).stream())
                .collect(Collectors.toList());
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
package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.AssigneeRequest;
import com.ftalk.samsu.payload.event.TaskRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeCriteriaRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeSubCriteriaRequest;
import com.ftalk.samsu.payload.gradePolicy.PolicyDocumentRequest;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.EventService;
import com.ftalk.samsu.service.GradePolicyService;
import com.ftalk.samsu.service.TaskService;
import com.ftalk.samsu.service.UserService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.event.AssigneeConstants;
import com.ftalk.samsu.utils.event.EventUtils;
import com.ftalk.samsu.utils.event.TaskConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.ftalk.samsu.utils.AppConstants.ID;

@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private GradePolicyService gradePolicyService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private PolicyDocumentRepository policyDocumentRepository;

    @Override
    public Task findTaskByEvent(Integer eventId, UserPrincipal currentUser) {
        return null;
    }

    private void taskValidate(TaskRequest taskRequest) {
        if (taskRequest.getEventId() == null) {
            throw new BadRequestException("EventId must not be null");
        }
        if (taskRequest.getGradeSubCriteriaId() == null) {
            throw new BadRequestException("EventId must not be null");
        }
    }

    @Override
    public Task createTask(TaskRequest taskRequest, UserPrincipal currentUser) {
        taskValidate(taskRequest);
        GradeSubCriteria gradeSubCriteria = gradePolicyService.getGradeSubCriteria(taskRequest.getGradeSubCriteriaId(), currentUser);
        Event event = eventService.getEvent(taskRequest.getEventId(), currentUser);
        User creator = userRepository.getUser(currentUser);
        if (taskRequest.getScore() < gradeSubCriteria.getMinScore() || taskRequest.getScore() > gradeSubCriteria.getMaxScore()) {
            throw new BadRequestException("Your score out of range GradeSubCriteria min-max score ["
                    + gradeSubCriteria.getMinScore() + ", " + gradeSubCriteria.getMaxScore() + "]");
        }
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
        return taskSaved;
    }

    @Override
    public Boolean isTaskStaff(Integer taskId, Integer userId) {
        Optional<Assignee> assignee = assigneeRepository.findById(new AssigneeId(taskId, userId));
        if (assignee.isPresent() && (AssigneeConstants.ACCEPT.getValue() == assignee.get().getStatus())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean checkPermissionCheckIn(Integer eventId, Integer userId) {
        Integer taskCheckinId = null;
        try {
            taskCheckinId = getTaskIdByTitle(eventId, "Check In");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return taskCheckinId != null ? isTaskStaff(taskCheckinId, userId) : false;
    }

    @Override
    public Set<String> getTaskStaff(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task", "Id", taskId));
        List<Assignee> assigneeList = task.getAssignees();
        if (assigneeList == null || assigneeList.isEmpty()) {
            throw new BadRequestException("Task don't have assignee");
        }
        return assigneeList.parallelStream().map(assignee -> assignee.getAssignee().getRollnumber()).collect(Collectors.toSet());
    }

    public Integer getTaskIdByTitle(Integer eventId, String title) {
        Task task = taskRepository.findTaskByEventIdAndTitle(eventId, title).orElseThrow(() -> new ResourceNotFoundException("Task Checkin", "Event Id", eventId));
        return task.getId();
    }

    @Override
    public Task updateTask(Integer id, TaskRequest taskRequest, UserPrincipal currentUser) {
        taskValidate(taskRequest);
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "Id", id));
        if (task.getEvent().getEventLeaderUser().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            if (!taskRequest.getGradeSubCriteriaId().equals(task.getGradeSubCriteria().getId())) {
                GradeSubCriteria gradeSubCriteria = gradePolicyService.getGradeSubCriteria(taskRequest.getGradeSubCriteriaId(), currentUser);
                task.setGradeSubCriteria(gradeSubCriteria);
            }
            if (!taskRequest.getEventId().equals(task.getEvent().getId())) {
                Event event = eventService.getEvent(taskRequest.getEventId(), currentUser);
                task.setEvent(event);
            }
            task.setTitle(taskRequest.getTitle());
            task.setContent(taskRequest.getContent());
            task.setScore(taskRequest.getScore());
            task.setDeadline(taskRequest.getDeadline());
            return taskRepository.save(task);
        }
        throw new UnauthorizedException("You don't have permission update this task!");
    }

    @Override
    @Transactional
    public Boolean updateTaskStatus(Integer id, Short status, UserPrincipal currentUser) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "Id", id));
        if (task.getEvent().getEventLeaderUser().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            if (Objects.equals(task.getStatus(), status)) {
                return Boolean.TRUE;
            }
//        boolean rollbackTaskStatus = task.getStatus() == TaskConstants.REVIEWED.getValue();
            task.setStatus(status);
            List<Assignee> assignees = task.getAssignees();
            if (task.getStatus() == TaskConstants.REVIEWED.getValue()) {
                updateScoreAssignee(assignees, task.getScore(), true);
            } else if (task.getStatus() == TaskConstants.WAITING.getValue()) {
                updateScoreAssignee(assignees, task.getScore(), false);
            }
            return Boolean.TRUE;
        }
        throw new UnauthorizedException("You don't have permission update this task!");
    }

    private void updateScoreAssignee(List<Assignee> assignees, Short score, boolean isPlus) {
        short tmp = isPlus ? (short) 1 : (short) -1;
        for (Assignee assignee : assignees) {
            if (assignee.getStatus() == AssigneeConstants.APPROVED.getValue()) {
                User user = assignee.getAssignee();
                user.setScore((short) (user.getScore() + score * tmp));
                userRepository.save(user);
            }
        }
    }

    public ApiResponse deleteTask(Integer id, UserPrincipal currentUser) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "Id", id));
        taskRepository.delete(task);
        return new ApiResponse(Boolean.TRUE, "Delete feedback question success");
    }


//    private updateAssignee()

//    @Override
//    public PagedResponse<PolicyDocument> getAllPolicyDocuments(int page, int size) {
//        AppUtils.validatePageNumberAndSize(page, size);
//
//        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, ID);
//        Page<PolicyDocument> events = policyDocumentRepository.findAll(pageable);
//
//        if (events.getNumberOfElements() == 0) {
//            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(),
//                    events.getTotalElements(), events.getTotalPages(), events.isLast());
//        }
//        return new PagedResponse<>(events.getContent(), events.getNumber(), events.getSize(), events.getTotalElements(),
//                events.getTotalPages(), events.isLast());
//    }
//
//    @Override
//    public PagedResponse<GradeCriteria> getAllGradeCriterias(int page, int size) {
//        AppUtils.validatePageNumberAndSize(page, size);
//
//        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, ID);
//        Page<GradeCriteria> events = gradeCriteriaRepository.findAll(pageable);
//
//        if (events.getNumberOfElements() == 0) {
//            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(),
//                    events.getTotalElements(), events.getTotalPages(), events.isLast());
//        }
//        return new PagedResponse<>(events.getContent(), events.getNumber(), events.getSize(), events.getTotalElements(),
//                events.getTotalPages(), events.isLast());
//    }
//
//    @Override
//    public PagedResponse<GradeSubCriteria> getAllGradeSubCriterias(int page, int size) {
//        AppUtils.validatePageNumberAndSize(page, size);
//
//        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, ID);
//        Page<GradeSubCriteria> events = gradeSubCriteriaRepository.findAll(pageable);
//
//        if (events.getNumberOfElements() == 0) {
//            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(),
//                    events.getTotalElements(), events.getTotalPages(), events.isLast());
//        }
//        return new PagedResponse<>(events.getContent(), events.getNumber(), events.getSize(), events.getTotalElements(),
//                events.getTotalPages(), events.isLast());
//    }
//
//    @Override
//    public PolicyDocument getPolicyDocument(Integer id, UserPrincipal currentUser) {
//        return policyDocumentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PolicyDocument", ID, id));
//    }
//
//    @Override
//    public GradeCriteria getGradeCriteria(Integer id, UserPrincipal currentUser) {
//        return gradeCriteriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("GradeCriteria", ID, id));
//    }
//
//    @Override
//    public GradeSubCriteria getGradeSubCriteria(Integer id, UserPrincipal currentUser) {
//        return gradeSubCriteriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("GradeSubCriteria", ID, id));
//    }
//
//    @Override
//    public PolicyDocument updatePolicyDocument(Integer id, PolicyDocumentRequest policyDocumentRequest, UserPrincipal currentUser) {
//        EventUtils.validateFileUrlsS3(policyDocumentRequest.getFileUrls());
//        PolicyDocument policyDocument = getPolicyDocument(id, currentUser);
//        policyDocument.setName(policyDocumentRequest.getName());
//        policyDocument.setFileUrls(policyDocumentRequest.getFileUrls());
//        return policyDocumentRepository.save(policyDocument);
//    }
//
//    @Override
//    public GradeCriteria updateGradeCriteria(Integer id, GradeCriteriaRequest gradeCriteriaRequest, UserPrincipal currentUser) {
//        GradeCriteria gradeCriteria = getGradeCriteria(id, currentUser);
//        PolicyDocument policyDocument = getPolicyDocument(gradeCriteriaRequest.getPolicyDocumentId(), currentUser);
//        gradeCriteria.setContent(gradeCriteriaRequest.getContent());
//        gradeCriteria.setPolicyDocument(policyDocument);
//        return gradeCriteriaRepository.save(gradeCriteria);
//    }
//
//    @Override
//    public GradeSubCriteria updateGradeSubCriteria(Integer id, GradeSubCriteriaRequest gradeSubCriteriaRequest, UserPrincipal currentUser) {
//        GradeSubCriteria gradeSubCriteria = getGradeSubCriteria(id, currentUser);
//        GradeCriteria gradeCriteria = getGradeCriteria(gradeSubCriteriaRequest.getGradeCriteriaId(), currentUser);
//        gradeSubCriteria.setContent(gradeSubCriteriaRequest.getContent());
//        gradeSubCriteria.setMinScore(gradeSubCriteriaRequest.getMinScore());
//        gradeSubCriteria.setMaxScore(gradeSubCriteriaRequest.getMaxScore());
//        gradeSubCriteria.setGradeCriteria(gradeCriteria);
//        return gradeSubCriteriaRepository.save(gradeSubCriteria);
//    }
//
//    @Override
//    public PolicyDocument addPolicyDocument(PolicyDocumentRequest policyDocumentRequest, UserPrincipal currentUser) {
//        EventUtils.validateFileUrlsS3(policyDocumentRequest.getFileUrls());
//        PolicyDocument policyDocument = new PolicyDocument(policyDocumentRequest.getName(), policyDocumentRequest.getFileUrls());
//        return policyDocumentRepository.save(policyDocument);
//    }
//
//    @Override
//    public GradeCriteria addGradeCriteria(GradeCriteriaRequest gradeCriteriaRequest, UserPrincipal currentUser) {
//        PolicyDocument policyDocument = getPolicyDocument(gradeCriteriaRequest.getPolicyDocumentId(), currentUser);
//        GradeCriteria gradeCriteria = new GradeCriteria(gradeCriteriaRequest.getContent(),policyDocument);
//        return gradeCriteriaRepository.save(gradeCriteria);
//    }
//
//    @Override
//    public GradeSubCriteria addGradeSubCriteria(GradeSubCriteriaRequest gradeSubCriteriaRequest, UserPrincipal currentUser) {
//        GradeCriteria gradeCriteria = getGradeCriteria(gradeSubCriteriaRequest.getGradeCriteriaId(), currentUser);
//        GradeSubCriteria gradeSubCriteria = new GradeSubCriteria(gradeSubCriteriaRequest.getContent(),
//                gradeSubCriteriaRequest.getMinScore(), gradeSubCriteriaRequest.getMaxScore(), gradeCriteria);
//        return gradeSubCriteriaRepository.save(gradeSubCriteria);
//    }


}

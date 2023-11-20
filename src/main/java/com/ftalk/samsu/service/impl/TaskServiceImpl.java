package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import com.ftalk.samsu.model.user.User;
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
import com.ftalk.samsu.utils.event.EventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

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
        Task task = new Task(taskRequest);
        task.setEvent(event);
        task.setGradeSubCriteria(gradeSubCriteria);
        task.setCreatorUserId(creator);
        Task taskSaved = taskRepository.save(task);
        Map<String, User> assigneeUser = userService.getMapUserByRollnumber(taskRequest.getAssignee());
        for (AssigneeRequest assigneeRequest : taskRequest.getAssignees()) {
            Assignee assignee = new Assignee(new AssigneeId(taskSaved.getId(), assigneeUser.get(assigneeRequest.getRollnumber()).getId()), assigneeRequest.getStatus());
            assigneeRepository.save(assignee);
        }
        return taskSaved;
    }

    @Override
    public Task updateTask(Integer id, TaskRequest taskRequest, UserPrincipal currentUser) {
        taskValidate(taskRequest);
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "Id", id));
        if (!taskRequest.getGradeSubCriteriaId().equals(task.getGradeSubCriteria().getId())) {
            GradeSubCriteria gradeSubCriteria = gradePolicyService.getGradeSubCriteria(taskRequest.getGradeSubCriteriaId(), currentUser);
            task.setGradeSubCriteria(gradeSubCriteria);
        }
        if (!taskRequest.getEventId().equals(task.getEvent().getId()) ){
            Event event = eventService.getEvent(taskRequest.getEventId(), currentUser);
            task.setEvent(event);
        }
        task.setTitle(taskRequest.getTitle());
        task.setContent(taskRequest.getContent());
        task.setScore(taskRequest.getScore());
        task.setStatus(taskRequest.getStatus());
        return taskRepository.save(task);
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

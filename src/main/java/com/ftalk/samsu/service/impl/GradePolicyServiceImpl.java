package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.*;
import com.ftalk.samsu.payload.group.GroupImportMemberResponse;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.payload.group.MemberImportFailed;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GradePolicyService;
import com.ftalk.samsu.service.GroupService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import com.ftalk.samsu.utils.event.EventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.ftalk.samsu.utils.AppConstants.CREATED_AT;
import static com.ftalk.samsu.utils.AppConstants.ID;

@Service
public class GradePolicyServiceImpl implements GradePolicyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradePolicyServiceImpl.class);
    @Autowired
    private GradeCriteriaRepository gradeCriteriaRepository;
    @Autowired
    private GradeSubCriteriaRepository gradeSubCriteriaRepository;
    @Autowired
    private PolicyDocumentRepository policyDocumentRepository;

    @Override
    public PagedResponse<PolicyDocument> getAllPolicyDocuments(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, ID);
        Page<PolicyDocument> events = policyDocumentRepository.findAll(pageable);

        if (events.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(),
                    events.getTotalElements(), events.getTotalPages(), events.isLast());
        }
        return new PagedResponse<>(events.getContent(), events.getNumber(), events.getSize(), events.getTotalElements(),
                events.getTotalPages(), events.isLast());
    }

    @Override
    public PagedResponse<GradeCriteriaResponse> getAllGradeCriterias(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, ID);
        Page<GradeCriteria> events = gradeCriteriaRepository.findAll(pageable);

        if (events.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(),
                    events.getTotalElements(), events.getTotalPages(), events.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(events.getContent(), GradeCriteriaResponse::new), events.getNumber(), events.getSize(), events.getTotalElements(),
                events.getTotalPages(), events.isLast());
    }

    @Override
    public PagedResponse<GradeSubCriteriaResponse> getAllGradeSubCriterias(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, ID);
        Page<GradeSubCriteria> events = gradeSubCriteriaRepository.findAll(pageable);

        if (events.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), events.getNumber(), events.getSize(),
                    events.getTotalElements(), events.getTotalPages(), events.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(events.getContent(), GradeSubCriteriaResponse::new), events.getNumber(), events.getSize(), events.getTotalElements(),
                events.getTotalPages(), events.isLast());
    }

    @Override
    public List<GradeCriteria> getAllGradeCriteria(){
        return gradeCriteriaRepository.findAll();
    }

    @Override
    public PolicyDocument getPolicyDocument(Integer id, UserPrincipal currentUser) {
        return policyDocumentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PolicyDocument", ID, id));
    }

    @Override
    public GradeCriteria getGradeCriteria(Integer id, UserPrincipal currentUser) {
        return gradeCriteriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("GradeCriteria", ID, id));
    }

    @Override
    public GradeSubCriteria getGradeSubCriteria(Integer id, UserPrincipal currentUser) {
        return gradeSubCriteriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("GradeSubCriteria", ID, id));
    }

    @Override
    public PolicyDocument updatePolicyDocument(Integer id, PolicyDocumentRequest policyDocumentRequest, UserPrincipal currentUser) {
        EventUtils.validateFileUrlsS3(policyDocumentRequest.getFileUrls());
        PolicyDocument policyDocument = getPolicyDocument(id, currentUser);
        policyDocument.setName(policyDocumentRequest.getName());
        policyDocument.setFileUrls(policyDocumentRequest.getFileUrls());
        return policyDocumentRepository.save(policyDocument);
    }

    @Override
    public GradeCriteria updateGradeCriteria(Integer id, GradeCriteriaRequest gradeCriteriaRequest, UserPrincipal currentUser) {
        GradeCriteria gradeCriteria = getGradeCriteria(id, currentUser);
        PolicyDocument policyDocument = getPolicyDocument(gradeCriteriaRequest.getPolicyDocumentId(), currentUser);
        gradeCriteria.setContent(gradeCriteriaRequest.getContent());
        gradeCriteria.setPolicyDocument(policyDocument);
        return gradeCriteriaRepository.save(gradeCriteria);
    }

    @Override
    public GradeSubCriteria updateGradeSubCriteria(Integer id, GradeSubCriteriaRequest gradeSubCriteriaRequest, UserPrincipal currentUser) {
        GradeSubCriteria gradeSubCriteria = getGradeSubCriteria(id, currentUser);
        GradeCriteria gradeCriteria = getGradeCriteria(gradeSubCriteriaRequest.getGradeCriteriaId(), currentUser);
        gradeSubCriteria.setContent(gradeSubCriteriaRequest.getContent());
        gradeSubCriteria.setMinScore(gradeSubCriteriaRequest.getMinScore());
        gradeSubCriteria.setMaxScore(gradeSubCriteriaRequest.getMaxScore());
        gradeSubCriteria.setGradeCriteria(gradeCriteria);
        return gradeSubCriteriaRepository.save(gradeSubCriteria);
    }

    @Override
    public PolicyDocument addPolicyDocument(PolicyDocumentRequest policyDocumentRequest, UserPrincipal currentUser) {
        EventUtils.validateFileUrlsS3(policyDocumentRequest.getFileUrls());
        PolicyDocument policyDocument = new PolicyDocument(policyDocumentRequest.getName(), policyDocumentRequest.getFileUrls());
        return policyDocumentRepository.save(policyDocument);
    }

    @Override
    public GradeCriteria addGradeCriteria(GradeCriteriaRequest gradeCriteriaRequest, UserPrincipal currentUser) {
        PolicyDocument policyDocument = getPolicyDocument(gradeCriteriaRequest.getPolicyDocumentId(), currentUser);
        GradeCriteria gradeCriteria = new GradeCriteria(gradeCriteriaRequest.getContent(),policyDocument);
        return gradeCriteriaRepository.save(gradeCriteria);
    }

    @Override
    public GradeSubCriteria addGradeSubCriteria(GradeSubCriteriaRequest gradeSubCriteriaRequest, UserPrincipal currentUser) {
        GradeCriteria gradeCriteria = getGradeCriteria(gradeSubCriteriaRequest.getGradeCriteriaId(), currentUser);
        GradeSubCriteria gradeSubCriteria = new GradeSubCriteria(gradeSubCriteriaRequest.getContent(),
                gradeSubCriteriaRequest.getMinScore(), gradeSubCriteriaRequest.getMaxScore(), gradeCriteria);
        return gradeSubCriteriaRepository.save(gradeSubCriteria);
    }


}

package com.ftalk.samsu.service;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.event.EventCreateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeCriteriaRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeSubCriteriaRequest;
import com.ftalk.samsu.payload.gradePolicy.PolicyDocumentRequest;
import com.ftalk.samsu.security.UserPrincipal;

public interface GradePolicyService {

    PagedResponse<PolicyDocument> getAllPolicyDocuments(int page, int size);

    PagedResponse<GradeCriteria> getAllGradeCriterias(int page, int size);

    PagedResponse<GradeSubCriteria> getAllGradeSubCriterias(int page, int size);

    PolicyDocument getPolicyDocument(Integer id, UserPrincipal currentUser);

    GradeCriteria getGradeCriteria(Integer id, UserPrincipal currentUser);

    GradeSubCriteria getGradeSubCriteria(Integer id, UserPrincipal currentUser);

    PolicyDocument updatePolicyDocument(Integer id, PolicyDocumentRequest policyDocumentRequest, UserPrincipal currentUser);

    GradeCriteria updateGradeCriteria(Integer id, GradeCriteriaRequest gradeCriteriaRequest, UserPrincipal currentUser);

    GradeSubCriteria updateGradeSubCriteria(Integer id, GradeSubCriteriaRequest gradeSubCriteriaRequest, UserPrincipal currentUser);

    PolicyDocument addPolicyDocument(PolicyDocumentRequest policyDocumentRequest, UserPrincipal currentUser);

    GradeCriteria addGradeCriteria(GradeCriteriaRequest gradeCriteriaRequest, UserPrincipal currentUser);

    GradeSubCriteria addGradeSubCriteria(GradeSubCriteriaRequest gradeSubCriteriaRequest, UserPrincipal currentUser);

}
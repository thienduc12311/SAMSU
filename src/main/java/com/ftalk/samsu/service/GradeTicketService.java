package com.ftalk.samsu.service;

import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketCreateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketUpdateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketResponse;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;

public interface GradeTicketService {
    PagedResponse<GradeTicketResponse> getAllGradeTickets(int page, int size);

    PagedResponse<GradeTicketResponse> getGradeTicketsByGradeSubCriteriaId(int page, int size, Integer gradeSubCriteriaId);

    GradeTicketResponse getGradeTicket(Integer id);

    GradeTicketResponse getGradeTicketByCode(String code);

    GradeTicketResponse updateGradeTicket(Integer id, GradeTicketUpdateRequest gradeTicketRequest, UserPrincipal currentUser);

    GradeTicketResponse updateGradeTicketV2(Integer id, GradeTicketUpdateRequest gradeTicketRequest, UserPrincipal currentUser);

    GradeTicketResponse addGradeTicket(GradeTicketCreateRequest gradeTicketRequest, UserPrincipal currentUser);

    ApiResponse deleteGradeTicket(Integer id, UserPrincipal currentUser);

    List<GradeTicket> finAllGradeTicketApproved(String semester, Integer uid);

    List<GradeTicket> finAllGradeTicketApproved(String semester);

    ApiResponse updateGradeTicketStatusByGuarantor(String code, Short status);

}

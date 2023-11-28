package com.ftalk.samsu.payload.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.Data;

@Data
public class GradeTicketResponse {
    private Integer id;
    private String title;
    private String content;
    private String evidenceUrls;
    private String feedback;
    private UserProfileReduce creator;
    private UserProfileReduce accepter;
    private GradeSubCriteriaResponse gradeSubCriteria;
    private Short status;

    private Integer score;

    public GradeTicketResponse(GradeTicket gradeTicket) {
        this.id = gradeTicket.getId();
        this.title = gradeTicket.getTitle();
        this.content = gradeTicket.getContent();
        this.evidenceUrls = gradeTicket.getEvidenceUrls();
        this.feedback = gradeTicket.getFeedback();
        this.creator = new UserProfileReduce(gradeTicket.getCreatorUser());
        this.accepter = gradeTicket.getAccepterUser() != null ? new UserProfileReduce(gradeTicket.getAccepterUser()) : null;
        this.gradeSubCriteria = gradeTicket.getGradeSubCriteria() != null ? new GradeSubCriteriaResponse(gradeTicket.getGradeSubCriteria()) : null;
        this.status = gradeTicket.getStatus();
        this.score = gradeTicket.getScore();
    }
}
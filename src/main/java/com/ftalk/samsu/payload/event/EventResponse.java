package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.utils.event.EventProposalConstants;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class EventResponse {
    private Integer id;
    private Short status;
    private Integer duration;
    private String title;
    private String content;
    private String creatorUserRollnumber;
    private Integer eventProposalId;
    private String eventLeaderUserRollnumber;
    private Semester semester;
    private String bannerUrl;
    private String fileUrls;
    private Date startTime;
    private List<Department> departments;
    private List<FeedbackQuestion> feedbackQuestions;
    private Date createAt;

    public EventResponse(Event event){
        id = event.getId();
        status = event.getStatus();
        duration = event.getDuration();
        title = event.getTitle();
        content = event.getContent();
        creatorUserRollnumber = event.getCreatorUser().getRollnumber();
        eventProposalId = event.getEventProposal() != null ? event.getEventProposal().getId() : null;
        eventLeaderUserRollnumber = event.getEventLeaderUser() != null ? event.getEventLeaderUser().getRollnumber() : null;
        semester = event.getSemester();
        bannerUrl = event.getBannerUrl();
        fileUrls = event.getFileUrls();
        departments = event.getDepartments();
        feedbackQuestions = event.getFeedbackQuestions();
        createAt = event.getCreatedAt();
    }


}

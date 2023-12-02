package com.ftalk.samsu.payload.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionResponse;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import com.ftalk.samsu.utils.ListConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class EventResponse implements Serializable {
    private static final long serialVersionUID = -2792369707368779346L;
    private Integer id;
    private Short status;
    private Integer duration;
    private String title;
    private String content;
    private UserProfileReduce creator;
    private Integer eventProposalId;
    private UserProfileReduce eventLeader;
    private Semester semester;
    private String bannerUrl;
    private String fileUrls;
    private Date startTime;
    private List<String> departments;
    private List<UserProfileReduce> participants;
    private List<FeedbackQuestionResponse> feedbackQuestions;
    private List<TaskResponse> tasks;
    private Date createAt;
    private Short attendScore;

    public EventResponse(Event event) {
        id = event.getId();
        status = event.getStatus();
        duration = event.getDuration();
        title = event.getTitle();
        content = event.getContent();
        creator = new UserProfileReduce(event.getCreatorUser().getName(), event.getCreatorUser().getUsername(),
                event.getCreatorUser().getAvatar(), event.getCreatorUser().getRollnumber());
        eventProposalId = event.getEventProposal() != null ? event.getEventProposal().getId() : null;
        eventLeader = event.getEventLeaderUser() != null ? new UserProfileReduce(event.getEventLeaderUser().getName(),
                event.getEventLeaderUser().getUsername(), event.getEventLeaderUser().getAvatar(), event.getEventLeaderUser().getRollnumber()) : null;
        semester = event.getSemester();
        bannerUrl = event.getBannerUrl();
        fileUrls = event.getFileUrls();
        departments = event.getDepartments() != null ? event.getDepartments().parallelStream().map(Department::getName).collect(Collectors.toList()) : null;
        feedbackQuestions = event.getFeedbackQuestions() != null ? event.getFeedbackQuestions().parallelStream().map(FeedbackQuestionResponse::new).collect(Collectors.toList()) : null;
        participants = event.getParticipants() != null ? event.getParticipants().parallelStream().map(UserProfileReduce::new).collect(Collectors.toList()) : null;
        createAt = event.getCreatedAt();
        startTime = event.getStartTime();
        attendScore = event.getAttendScore();
        tasks = event.getTasks() != null ? ListConverter.listToList(event.getTasks(), TaskResponse::new) : null;
    }
}

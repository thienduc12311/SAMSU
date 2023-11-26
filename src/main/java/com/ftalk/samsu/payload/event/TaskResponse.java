package com.ftalk.samsu.payload.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.gradePolicy.GradeSubCriteriaResponse;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TaskResponse {
    private Integer id;
    private String title;
    private String content;
    private Short status;
    private List<UserProfileReduce> assignees;
    private UserProfileReduce creator;
    private Integer eventId;
    private Short score;
    private GradeSubCriteriaResponse gradeSubCriteria;

    public TaskResponse(Task task) {
        id = task.getId();
        title = task.getTitle();
        content = task.getContent();
        status = task.getStatus();
        creator = task.getCreatorUserId() != null ? new UserProfileReduce(task.getCreatorUserId()) : null;
        assignees = task.getAssignees().stream().map(assignee -> new UserProfileReduce(assignee.getAssignee())).collect(Collectors.toList());
        eventId = task.getEvent() != null ? task.getEvent().getId() : null;
        score = task.getScore();
        gradeSubCriteria = new GradeSubCriteriaResponse(task.getGradeSubCriteria());
    }


}

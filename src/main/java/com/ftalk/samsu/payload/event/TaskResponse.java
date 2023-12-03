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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse implements Serializable {
    private static final long serialVersionUID = 5975439545772267190L;
    private Integer id;
    private String title;
    private String content;
    private Short status;
    private List<AssigneeResponseWithoutTask> assignees;
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
        assignees = task.getAssignees() != null ? task.getAssignees().stream().map(AssigneeResponseWithoutTask::new).collect(Collectors.toList()) : null;
        eventId = task.getEvent() != null ? task.getEvent().getId() : null;
        score = task.getScore();
        gradeSubCriteria = new GradeSubCriteriaResponse(task.getGradeSubCriteria());
    }


}

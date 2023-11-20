package com.ftalk.samsu.payload.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private String title;
    private String content;
    private Short status;
    private Short score;
    private Integer gradeSubCriteriaId;
    /// <summary>Not need when create event</summary>
    private Integer eventId;
    private List<AssigneeRequest> assignees;

    public Set<String> getAssignee() {
        return assignees.parallelStream().map(AssigneeRequest::getRollnumber).collect(Collectors.toSet());
    }
}

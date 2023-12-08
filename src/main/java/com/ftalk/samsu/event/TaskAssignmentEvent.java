package com.ftalk.samsu.event;

import com.google.type.DateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class TaskAssignmentEvent extends ApplicationEvent {
    private Set<Integer> assigneeIds;
    private String title;
    private Date deadline;
    private String content;
    public TaskAssignmentEvent(Object source, Set<Integer> assigneeIds, String title, Date deadline) {
        super(source);
        this.assigneeIds = assigneeIds;
        this.title = title;
        this.deadline = deadline;
        content = "Bạn được giao task " + title + "cho sự kiện with deadline: " + deadline;
    }

}

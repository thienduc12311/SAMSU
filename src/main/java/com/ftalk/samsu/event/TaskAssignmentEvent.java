package com.ftalk.samsu.event;

import com.google.type.DateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class TaskAssignmentEvent extends ApplicationEvent {
    private String title;
    private DateTime deadline;
    public TaskAssignmentEvent(Object source, String title, DateTime deadline) {
        super(source);
        this.title = title;
        this.deadline = deadline;
    }
}

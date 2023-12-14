package com.ftalk.samsu.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.Set;

@Setter
@Getter
public class NotificationEvent extends ApplicationEvent {
    private Set<Integer> assigneeIds;
    private String title;
    private String content;
    private String image;
    public NotificationEvent(Object source, Set<Integer> assigneeIds, String title, String content, String image) {
        super(source);
        this.assigneeIds = assigneeIds;
        this.title = title;
        this.content = content;
        this.image = image;
    }
}

package com.ftalk.samsu.event;

import org.springframework.context.ApplicationEvent;

public class NotificationEvent extends ApplicationEvent {
    public NotificationEvent(Object source) {
        super(source);
    }
}

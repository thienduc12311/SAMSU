package com.ftalk.samsu.event;

import com.ftalk.samsu.payload.notification.NotificationCreateRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Getter
@Setter
public class NotificationCreateEvent extends ApplicationEvent {
    private NotificationCreateRequest notificationCreateRequest;
    private Set<Integer> receiverIds;
    public NotificationCreateEvent(Object source, NotificationCreateRequest notificationCreateRequest, Set<Integer> receiverIds) {
        super(source);
        this.notificationCreateRequest = notificationCreateRequest;
        this.receiverIds = receiverIds;
    }
}

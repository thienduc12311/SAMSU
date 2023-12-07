package com.ftalk.samsu.payload.notification;

import lombok.Data;

@Data
public class NotificationCreateRequest {
    private Short type;
    private String title;
    private String content;
}

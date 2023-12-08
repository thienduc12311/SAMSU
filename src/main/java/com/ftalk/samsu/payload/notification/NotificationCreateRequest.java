package com.ftalk.samsu.payload.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationCreateRequest {
    private Short type;
    private String title;
    private String content;
}

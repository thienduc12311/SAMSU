package com.ftalk.samsu.payload.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSendGroupRequest {
    private String title;
    private String content;
    private String image;
    private Boolean isSendNotification;
    private Boolean isSendEmail;
}

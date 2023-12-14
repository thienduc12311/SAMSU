package com.ftalk.samsu.payload.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSendRequest {
    private String title;
    private String content;
    private String image;
    private Set<String> rollnumbers;
    private Boolean isSendNotification;
    private Boolean isSendEmail;
}

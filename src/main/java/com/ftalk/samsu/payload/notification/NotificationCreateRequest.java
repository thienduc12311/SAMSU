package com.ftalk.samsu.payload.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {
    private Short type;
    private String title;
    private String content;
}

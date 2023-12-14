package com.ftalk.samsu.payload.notification;

import com.ftalk.samsu.model.announcement.Announcement;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.Data;


@Data
public class NotificationResponse {
    private Integer id;
    private Short type;
    private String title;
    private String content;
    private UserProfileReduce creator;
    public NotificationResponse(Announcement announcement) {
        this.id = announcement.getId();
        this.type = announcement.getType();
        this.title = announcement.getTitle();
        this.content = announcement.getContent();
        this.creator = announcement.getCreatorUser() != null ? new UserProfileReduce(announcement.getCreatorUser()) : null;
    }
}

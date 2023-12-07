package com.ftalk.samsu.model.announcement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserNotificationId implements Serializable {
    @Column(name = "announcement_id")
    private Integer announcementId;
    @Column(name = "receiver_users_id")
    private Integer receivedUserId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotificationId that = (UserNotificationId) o;
        return Objects.equals(announcementId, that.announcementId) && Objects.equals(receivedUserId, that.receivedUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(announcementId, receivedUserId);
    }
}

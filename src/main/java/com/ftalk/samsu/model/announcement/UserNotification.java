package com.ftalk.samsu.model.announcement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.repository.AnnouncementRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_notifications")
public class UserNotification extends DateAudit implements Serializable {
    @EmbeddedId
    private UserNotificationId id;
    @Column
    private Short status;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", insertable = false, updatable = false)
    private Announcement announcement;

    public UserNotification(UserNotificationId id, Short status) {
        this.id = id;
        this.status = status;
    }
}

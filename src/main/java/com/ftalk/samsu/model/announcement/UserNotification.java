package com.ftalk.samsu.model.announcement;

import com.ftalk.samsu.model.audit.DateAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@Table(name = "user_notifications")
public class UserNotification extends DateAudit implements Serializable {
    @EmbeddedId
    private UserNotificationId id;
    @Column
    private Short status;
}

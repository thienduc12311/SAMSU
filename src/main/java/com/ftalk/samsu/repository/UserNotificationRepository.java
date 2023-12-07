package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.announcement.UserNotification;
import com.ftalk.samsu.model.announcement.UserNotificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UserNotificationId>{
}

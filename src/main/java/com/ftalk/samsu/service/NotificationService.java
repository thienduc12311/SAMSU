package com.ftalk.samsu.service;

import com.ftalk.samsu.model.announcement.Announcement;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.notification.NotificationCreateRequest;
import com.ftalk.samsu.payload.notification.NotificationResponse;
import com.ftalk.samsu.payload.notification.NotificationUpdateRequest;
import com.ftalk.samsu.payload.notification.TokenResponse;
import com.ftalk.samsu.security.UserPrincipal;
import com.google.firebase.messaging.BatchResponse;

import java.util.concurrent.ExecutionException;


public interface NotificationService {
    PagedResponse<NotificationResponse> getAllNotifications(int page, int size);

    PagedResponse<NotificationResponse> getNotificationByUser(Integer id, int page, int size);

    NotificationResponse getNotification(Integer id);
    NotificationResponse addNotification(NotificationCreateRequest announcement, UserPrincipal currentUser);
    NotificationResponse updateNotification(Integer id, NotificationUpdateRequest announcement, UserPrincipal currentUser);
    void deleteNotification(Integer id);
    BatchResponse pushNotification(Integer announcementId) throws ExecutionException, InterruptedException;

    TokenResponse addFcmToken(String fcmToken, UserPrincipal currentUser) throws ExecutionException, InterruptedException;

    TokenResponse deleteFcmToken(String fcmToken, UserPrincipal currentUser) throws ExecutionException, InterruptedException;
}

package com.ftalk.samsu.controller.notification;

import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.notification.*;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.NotificationService;
import com.ftalk.samsu.service.UserService;
import com.ftalk.samsu.utils.AppConstants;
import com.google.firebase.messaging.BatchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<PagedResponse<NotificationResponse>> getAll(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<NotificationResponse> response = notificationService.getAllNotifications(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<PagedResponse<NotificationResponse>> getNotificationByUser(
            @PathVariable(name = "id") Integer id,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<NotificationResponse> response = notificationService.getNotificationByUser(id, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<PagedResponse<NotificationResponse>> getNotificationByUser(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<NotificationResponse> response = notificationService.getNotificationByUser(currentUser.getId(), page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> get(@PathVariable(name = "id") Integer id) {
        NotificationResponse notification = notificationService.getNotification(id);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@RequestBody NotificationCreateRequest notificationCreateRequest,
                                                      @CurrentUser UserPrincipal currentUser) {
        NotificationResponse notification = notificationService.addNotification(notificationCreateRequest, currentUser);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    @PostMapping("token")
    public ResponseEntity<TokenResponse> addFcmToken(@RequestBody TokenAddRequest fcmToken, @CurrentUser UserPrincipal currentUser) throws ExecutionException, InterruptedException {
        TokenResponse result = notificationService.addFcmToken(fcmToken.getToken(), currentUser);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> update(@PathVariable(name = "id") Integer id,
                                                      @RequestBody NotificationUpdateRequest notificationUpdateRequest, @CurrentUser UserPrincipal currentUser) {
        NotificationResponse notification = notificationService.updateNotification(id, notificationUpdateRequest, currentUser);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }



}

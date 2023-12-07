package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.model.announcement.Announcement;
import com.ftalk.samsu.model.announcement.UserNotification;
import com.ftalk.samsu.model.announcement.UserNotificationId;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.notification.NotificationCreateRequest;
import com.ftalk.samsu.payload.notification.NotificationResponse;
import com.ftalk.samsu.payload.notification.NotificationUpdateRequest;
import com.ftalk.samsu.repository.AnnouncementRepository;
import com.ftalk.samsu.repository.UserNotificationRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.NotificationService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private FirebaseMessaging firebaseMessaging;
    @Autowired
    private Firestore firestore;

    @Override
    public PagedResponse<NotificationResponse> getAllNotifications(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");

        Page<Announcement> announcements = announcementRepository.findAll(pageable);

        List<Announcement> content = announcements.getNumberOfElements() == 0 ? Collections.emptyList() : announcements.getContent();

        return getNotificationResponse(announcements);
    }

    private PagedResponse<NotificationResponse> getNotificationResponse(Page<Announcement> announcements) {
        if (announcements.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), announcements.getNumber(), announcements.getSize(), announcements.getTotalElements(), announcements.getTotalPages(), announcements.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(announcements.getContent(), NotificationResponse::new), announcements.getNumber(), announcements.getSize(), announcements.getTotalElements(), announcements.getTotalPages(), announcements.isLast());
    }

    @Override
    public NotificationResponse getNotification(Integer id) {
        Announcement announcement = announcementRepository.findById(id).orElseThrow(() -> new BadRequestException("GradeTicket not found with id " + id));
        return new NotificationResponse(announcement);
    }

    @Override
    public NotificationResponse addNotification(NotificationCreateRequest announcement, UserPrincipal currentUser) {
        User creator = userRepository.getUser(currentUser);
        Announcement newAnnouncement = new Announcement(announcement.getType(), announcement.getTitle(), announcement.getContent(), creator);
        Announcement savedAnnouncement = announcementRepository.save(newAnnouncement);
        return new NotificationResponse(savedAnnouncement);
    }

    @Override
    public NotificationResponse updateNotification(Integer id, NotificationUpdateRequest notificationUpdateRequest, UserPrincipal currentUser) {
        User creator = userRepository.getUser(currentUser);
        Announcement announcement = announcementRepository.findById(id).orElseThrow(() -> new BadRequestException("Announcement not found with id " + id));
        if (notificationUpdateRequest.getTitle() != null )
            announcement.setTitle(notificationUpdateRequest.getTitle());
        if (notificationUpdateRequest.getContent() != null)
            announcement.setContent(notificationUpdateRequest.getContent());
        if (notificationUpdateRequest.getType() != null)
            announcement.setType(notificationUpdateRequest.getType());
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        return new NotificationResponse(savedAnnouncement);

    }

    @Override
    public void deleteNotification(Integer id) {
        Announcement announcement = announcementRepository.findById(id).orElseThrow(() -> new BadRequestException("Announcement not found with id " + id));
        announcementRepository.delete(announcement);
    }

    @Override
    public BatchResponse pushNotification(Integer announcementId) throws ExecutionException, InterruptedException {
        List<String> registrationTokens= new ArrayList<>();
        Announcement announcement = announcementRepository.findById(announcementId).orElseThrow(() -> new BadRequestException("Announcement not found with id " + announcementId));
        ApiFuture<QuerySnapshot> future = firestore.collection("users").get();
// future.get() blocks on response
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            if (userNotificationRepository.existsById(new UserNotificationId(announcementId, Integer.parseInt(document.getId())))) {
                registrationTokens.addAll(getListFcmToken(document.getReference()));
            }
        }
        Notification notification = Notification.builder()
                .setTitle(announcement.getTitle())
                .setBody(announcement.getContent())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(registrationTokens)
                .build();
//        Message message = Message.builder()
//                .setNotification(notification)
//                .setToken(registrationTokens.get(0))
//                .build();

        BatchResponse batchResponse = null;
        try {
            batchResponse = firebaseMessaging.sendEachForMulticast(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

        return batchResponse;
    }

    @Override
    public boolean addFcmToken(String fcmToken, UserPrincipal currentUser) throws ExecutionException, InterruptedException {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with jwt token: %s", currentUser.getEmail())));
// ...
// query.get() blocks on response
        DocumentReference docRef = firestore.collection("users").document(user.getId().toString());
        List<String> listFcmToken = getListFcmToken(docRef);
        if (!listFcmToken.contains(fcmToken)) listFcmToken.add(fcmToken);
        Map<String, List<String>> data = new HashMap<>();
        data.put("tokens", listFcmToken);
        ApiFuture<WriteResult> result = docRef.set(data);
        return true;
    }

    private List<String> getListFcmToken(DocumentReference docRef) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> documentSnapshot = docRef.get();
        DocumentSnapshot document = documentSnapshot.get();
        List<String> listFcmToken = new ArrayList<>();
        if (document.exists()) {
            Object tokens = document.get("tokens");
            if (tokens != null) {
                listFcmToken = (List<String>) tokens ;
            }
        }
        return listFcmToken;
    }
}

package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.event.MailEvent;
import com.ftalk.samsu.event.NotificationCreateEvent;
import com.ftalk.samsu.event.NotificationEvent;
import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.model.announcement.Announcement;
import com.ftalk.samsu.model.announcement.UserNotification;
import com.ftalk.samsu.model.announcement.UserNotificationId;
import com.ftalk.samsu.model.announcement.UserToken;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.notification.*;
import com.ftalk.samsu.repository.AnnouncementRepository;
import com.ftalk.samsu.repository.GroupRepository;
import com.ftalk.samsu.repository.UserNotificationRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.NotificationService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.messaging.*;
import org.bouncycastle.tsp.TSPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private FirebaseMessaging firebaseMessaging;
    @Autowired
    private Firestore firestore;
    @Autowired
    private ApplicationEventMulticaster eventPublisher;

    @Override
    public PagedResponse<NotificationResponse> getAllNotifications(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");

        Page<Announcement> announcements = announcementRepository.findAll(pageable);

        List<Announcement> content = announcements.getNumberOfElements() == 0 ? Collections.emptyList() : announcements.getContent();

        return getNotificationResponse(announcements);
    }

    @Override
    public PagedResponse<NotificationResponse> getNotificationByUser(Integer id, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");

        Page<UserNotification> userNotifications = userNotificationRepository.findByIdReceivedUserId(id, pageable);

        List<Announcement> content = userNotifications.getNumberOfElements() == 0 ? Collections.emptyList() : userNotifications.getContent().stream().map(UserNotification::getAnnouncement).collect(Collectors.toList());
        Page<Announcement> announcements = new PageImpl<>(content);
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
        if (notificationUpdateRequest.getTitle() != null)
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
        List<String> registrationTokens = new ArrayList<>();
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
            System.out.println("Firebase error");
        }

        return batchResponse;
    }

    @Override
    public TokenResponse addFcmToken(String fcmToken, UserPrincipal currentUser) throws ExecutionException, InterruptedException {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with jwt token: %s", currentUser.getEmail())));
        if (fcmToken == null) throw new BadRequestException("Token is null");
// ...
// query.get() blocks on response
        DocumentReference docRef = firestore.collection("users").document(user.getId().toString());
        List<String> listFcmToken = getListFcmToken(docRef);
        if (!listFcmToken.contains(fcmToken)) listFcmToken.add(fcmToken);
        Map<String, List<String>> data = new HashMap<>();
        data.put("tokens", listFcmToken);
        ApiFuture<WriteResult> result = docRef.set(data);
        return new TokenResponse(user.getId(), fcmToken);
    }

    @Override
    public TokenResponse deleteFcmToken(String fcmToken, UserPrincipal currentUser) throws ExecutionException, InterruptedException {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with jwt token: %s", currentUser.getEmail())));
        if (fcmToken == null) throw new BadRequestException("Token is null");
        DocumentReference docRef = firestore.collection("users").document(user.getId().toString());
        List<String> listFcmToken = getListFcmToken(docRef);
        listFcmToken.remove(fcmToken);
        Map<String, List<String>> data = new HashMap<>();
        data.put("tokens", listFcmToken);
        ApiFuture<WriteResult> result = docRef.set(data);
        return new TokenResponse(user.getId(), fcmToken);
    }

    @Override
    public ApiResponse sendNotificationToGroup(Integer id, NotificationSendGroupRequest notificationSendRequest) throws ExecutionException, InterruptedException {
        if (notificationSendRequest.getIsSendNotification() == null) notificationSendRequest.setIsSendNotification(false);
        if (notificationSendRequest.getIsSendEmail() == null) notificationSendRequest.setIsSendEmail(false);
        boolean isSendNotification = notificationSendRequest.getIsSendNotification();
        boolean isSendEmail = notificationSendRequest.getIsSendEmail();
        if (!isSendNotification && !isSendEmail) return new ApiResponse(false, "Please choose at least one way to send notification");
        Group group = groupRepository.findById(id).orElseThrow(() -> new BadRequestException("Group not found with id " + id));
        if (isSendNotification) {
            Set<Integer> userIds = group.getUsers().stream().map(User::getId).collect(Collectors.toSet());
            System.out.println("Binhhhh");
            System.out.println(userIds);
            eventPublisher.multicastEvent(new NotificationEvent(this, userIds, notificationSendRequest.getTitle(), notificationSendRequest.getContent(), notificationSendRequest.getImage()));
        }
        if (isSendEmail) {
            Set<String> emails = group.getUsers().stream().map(User::getEmail).collect(Collectors.toSet());
            eventPublisher.multicastEvent(new MailEvent(this, emails, notificationSendRequest.getTitle(), notificationSendRequest.getContent()));
        }

        return new ApiResponse(true, "Notification sent");
    }

    @Override
    public ApiResponse sendNotification(NotificationSendRequest notificationSendRequest) throws ExecutionException, InterruptedException {
        if (notificationSendRequest.getIsSendNotification() == null) notificationSendRequest.setIsSendNotification(false);
        if (notificationSendRequest.getIsSendEmail() == null) notificationSendRequest.setIsSendEmail(false);
        boolean isSendNotification = notificationSendRequest.getIsSendNotification();
        boolean isSendEmail = notificationSendRequest.getIsSendEmail();
        if (!isSendNotification && !isSendEmail) return new ApiResponse(false, "Please choose at least one way to send notification");
        Set<User> users = userRepository.findAllByRollnumberIn(notificationSendRequest.getRollnumbers());
        if (isSendNotification) {
            Set<Integer> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
            eventPublisher.multicastEvent(new NotificationEvent(this, userIds, notificationSendRequest.getTitle(), notificationSendRequest.getContent(), notificationSendRequest.getImage()));
        }
        if (isSendEmail) {
            Set<String> emails = users.stream().map(User::getEmail).collect(Collectors.toSet());
            eventPublisher.multicastEvent(new MailEvent(this, emails, notificationSendRequest.getTitle(), notificationSendRequest.getContent()));
        }
        return new ApiResponse(true, "Notification sent");
    }

    private List<String> getListFcmToken(DocumentReference docRef) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> documentSnapshot = docRef.get();
        DocumentSnapshot document = documentSnapshot.get();
        List<String> listFcmToken = new ArrayList<>();
        if (document.exists()) {
            UserToken tokens = document.toObject(UserToken.class);
            if (tokens != null) {
                listFcmToken = tokens.getTokens();
            }
        }
        return listFcmToken;
    }

    @EventListener
    private void handleNotificationEvent(NotificationEvent event) throws ExecutionException, InterruptedException {
        List<String> registrationTokens = new ArrayList<>();
        Set<Integer> assigneeIds = event.getAssigneeIds();
        String title = event.getTitle();
        String content = event.getContent();
        String image = event.getImage();
        Notification notification;
        if (image == null || image.isEmpty())
            notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();
        else
            notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .setImage(image)
                .build();
        if (assigneeIds == null) {
            Message message = Message.builder()
                    .setNotification(notification)
                    .setTopic("all")
                    .build();
            try {
                firebaseMessaging.send(message);
//                NotificationCreateRequest notificationCreateRequest = new NotificationCreateRequest((short) 0, title, content);
//                eventPublisher.multicastEvent(new NotificationCreateEvent(this, notificationCreateRequest, assigneeIds));
            } catch (Exception e) {
                System.out.println("Firebase error");
            }
        }
        else {
            for (Integer assigneeId : assigneeIds) {
                User user = userRepository.findById(assigneeId).orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with id: %s", assigneeId)));
                DocumentReference docRef = firestore.collection("users").document(user.getId().toString());
                List<String> listFcmToken = getListFcmToken(docRef);
                registrationTokens.addAll(listFcmToken);
            }
        }
        if (registrationTokens.isEmpty()) return;
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(registrationTokens)
                .build();
        try {
            firebaseMessaging.sendEachForMulticast(message);
            NotificationCreateRequest notificationCreateRequest = new NotificationCreateRequest((short) 1, title, content);

            eventPublisher.multicastEvent(new NotificationCreateEvent(this, notificationCreateRequest, assigneeIds));
        } catch (Exception e) {
            System.out.println("Firebase error");
        }

    }

    @EventListener
    private void handleCreateNotificationEvent(NotificationCreateEvent event) {
        NotificationCreateRequest notificationCreateRequest = event.getNotificationCreateRequest();
        User user = userRepository.findById(6).orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with id: %s", 6)));
        Announcement newAnnouncement = new Announcement(notificationCreateRequest.getType(), notificationCreateRequest.getTitle(), notificationCreateRequest.getContent(), user);
        Announcement savedAnnouncement = announcementRepository.save(newAnnouncement);
        Set<Integer> assigneeIds = event.getReceiverIds();
        List<UserNotification> userNotifications = new ArrayList<>();
        if (assigneeIds == null) return;
        for (Integer assigneeId : assigneeIds) {
            UserNotification userNotification = new UserNotification(new UserNotificationId(savedAnnouncement.getId(), assigneeId), ( short)1);
            userNotifications.add(userNotification);
        }
        userNotificationRepository.saveAll(userNotifications);
    }
}
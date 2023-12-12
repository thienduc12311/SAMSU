package com.ftalk.samsu.utils.notification;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationConstant {
    public static final String NOTIFICATION_TASK_TITLE = "You have a new task";
    public static final String NOTIFICATION_NEW_EVENT_TITLE = "New event is coming";
    static final String pattern = "HH:mm dd-MM-yyyy";
    static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    public static  String genTaskAssignmentNotificationContent(String task, String event, Date deadline) {
        return "You have been assigned task " + task + " for event " + event + "\nDeadline: " + simpleDateFormat.format(deadline);
    }

    public static final String NOTIFICATION_EVENT_TITLE = "Reminding";
    public static String genEventNotificationCheckinContent(String event) {
        return "Event " + event + " will start in a few minutes. Don't miss it!";
    }

    public static String genEventNotificationCheckoutContent(String event) {
        return "Event " + event + " will finish soon. Don't forget to check out!";
    }

    public static String genEventNotificationContent(String event) {
        return "Event " + event + " is coming. Register now and explore it!";
    }

}

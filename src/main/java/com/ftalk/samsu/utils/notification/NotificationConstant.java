package com.ftalk.samsu.utils.notification;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationConstant {
    public static final String NOTIFICATION_TASK_TITLE = "Bạn có một công việc mới";
    static final String pattern = "HH:mm dd-MM-yyyy";
    static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    public static  String genTaskAssignmentNotificationContent(String task, String event, Date deadline) {
        return "Bạn vừa được giao công việc " + task + " cho sự kiện " + event + "\nThời hạn hoàn thành: " + simpleDateFormat.format(deadline);
    }

    public static final String NOTIFICATION_EVENT_TITLE = "Nhắc bạn";
    public static String genEventNotificationCheckinContent(String event) {
        return "Sự kiện " + event + " sẽ diễn ra trong vài phút nữa. Đừng bỏ lỡ bạn nhé!";
    }

    public static String genEventNotificationCheckoutContent(String event) {
        return "Sự kiện " + event + " đã sắp kết thúc. Đừng quên checkout bạn nhé!";
    }

    public static String genEventNotificationContent(String event) {
        return "Sự kiện " + event + " sắp diễn ra. Đăng ký ngay để cùng nhau trải nghiệm bạn nhé!";
    }

}

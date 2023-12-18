package com.ftalk.samsu.event;

import com.ftalk.samsu.payload.achievement.TaskListRequest;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import com.ftalk.samsu.security.UserPrincipal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AssigneeCertificateCreateEvent extends ApplicationEvent {
    private String eventTitle;
    private String semesterName;
    private Date eventDate;
    private List<TaskListRequest> taskList;
    UserPrincipal currentUser;

    public AssigneeCertificateCreateEvent(Object source, String eventTitle, String semesterName, Date eventDate, List<TaskListRequest> taskList, UserPrincipal currentUser) {
        super(source);
        this.eventTitle = eventTitle;
        this.semesterName = semesterName;
        this.eventDate = eventDate;
        this.taskList = taskList;
        this.currentUser = currentUser;
    }
}

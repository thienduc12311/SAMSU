package com.ftalk.samsu.event;

import com.ftalk.samsu.payload.user.UserProfileReduce;
import com.ftalk.samsu.security.UserPrincipal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ParticipantCertificateCreateEvent extends ApplicationEvent {
    private String eventTitle;
    private String semesterName;
    private Date eventDate;
    private List<UserProfileReduce> participants;
    UserPrincipal currentUser;
    public ParticipantCertificateCreateEvent(Object source, String eventTitle, String semesterName, Date eventDate, List<UserProfileReduce> participants, UserPrincipal currentUser) {
        super(source);
        this.eventTitle = eventTitle;
        this.semesterName = semesterName;
        this.eventDate = eventDate;
        this.participants = participants;
        this.currentUser = currentUser;
    }
}

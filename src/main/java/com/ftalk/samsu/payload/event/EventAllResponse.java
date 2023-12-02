package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import com.ftalk.samsu.utils.ListConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class EventAllResponse implements Serializable {
    private static final long serialVersionUID = -2792369707368779346L;
    private Integer id;
    private Short status;
    private Integer duration;
    private String title;
    private UserProfileReduce creator;
    private Semester semester;
    private String bannerUrl;
    private Date startTime;
    private Date createAt;
    private Short attendScore;

    public EventAllResponse(Event event) {
        id = event.getId();
        status = event.getStatus();
        duration = event.getDuration();
        title = event.getTitle();
        creator = new UserProfileReduce(event.getCreatorUser().getName(), event.getCreatorUser().getUsername(),
                event.getCreatorUser().getAvatar(), event.getCreatorUser().getRollnumber());
        semester = event.getSemester();
        bannerUrl = event.getBannerUrl();
        createAt = event.getCreatedAt();
        startTime = event.getStartTime();
        attendScore = event.getAttendScore();
    }
}

package com.ftalk.samsu.payload.achievement;

import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantAchievementCreateRequest {
    private String eventTitle;
    private String semesterName;
    private Date eventDate;
    private List<UserProfileReduce> participants;


}

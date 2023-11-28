package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {
    private Integer eventId;
    private UserProfileReduce user;
    private Date checkin;
    private Date checkout;

}

package com.ftalk.samsu.payload.gradePolicy;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeAllEntryResponse {
    //    private Short score;
    private ConcurrentHashMap<Integer, Short> scoreWithSubCriteria;
    private UserProfileReduce userProfileReduce;

    public GradeAllEntryResponse(User user) {
        this.userProfileReduce = new UserProfileReduce(user);
        scoreWithSubCriteria = new ConcurrentHashMap<>();
    }

    public void addScoreWithSubCriteriaId(Integer id, Short score) {
        scoreWithSubCriteria.merge(id, score, (a, b) -> (short) (a + b));
    }
}

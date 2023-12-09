package com.ftalk.samsu.payload.achievement;

import com.ftalk.samsu.model.achievement.AchievementTemplate;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class AchievementRequest {
    private String title;
    private String content;
    private String url;
    private Integer achievementTemplateId;
    private String semesterName;
    private String ownerRollnumber;
}

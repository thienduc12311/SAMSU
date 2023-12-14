package com.ftalk.samsu.payload.achievement;


import com.ftalk.samsu.model.achievement.Achievement;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AchievementResponse {
    private Integer id;
    private String title;
    private String content;
    private String url;
    private AchievementTemplateResponse achievementTemplate;
    private String semesterName;
    private UserProfileReduce owner;
    private UserProfileReduce creator;

    public AchievementResponse(Achievement achievement) {
        this.id = achievement.getId();
        this.title = achievement.getTitle();
        this.content = achievement.getContent();
        this.url = achievement.getUrl();
        this.achievementTemplate = achievement.getAchievementTemplate() != null ? new AchievementTemplateResponse(achievement.getAchievementTemplate()) : null ;
        this.semesterName = achievement.getSemester() != null ? achievement.getSemester().getName() : null;
        this.owner = new UserProfileReduce(achievement.getOwner());
        this.creator = new UserProfileReduce(achievement.getCreator());
    }
}

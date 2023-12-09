package com.ftalk.samsu.payload.achievement;

import com.ftalk.samsu.model.achievement.AchievementTemplate;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Size;

@Data
public class AchievementTemplateResponse {
    private Integer id;
    private String title;
    private String content;

    public AchievementTemplateResponse(AchievementTemplate achievementTemplate) {
        this.id = achievementTemplate.getId();
        this.title = achievementTemplate.getTitle();
        this.content = achievementTemplate.getContent();
    }
}

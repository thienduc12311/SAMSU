package com.ftalk.samsu.payload.achievement;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class AchievementTemplateRequest {
    private String title;
    private String content;
}

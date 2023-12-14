package com.ftalk.samsu.service;

import com.ftalk.samsu.model.achievement.Achievement;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.achievement.AchievementRequest;
import com.ftalk.samsu.payload.achievement.AchievementResponse;
import com.ftalk.samsu.payload.achievement.AchievementTemplateRequest;
import com.ftalk.samsu.payload.achievement.AchievementTemplateResponse;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;

public interface AchievementService {
    PagedResponse<AchievementResponse> getAllAchievementResponse(int page, int size);
    List<AchievementResponse> getAchievementResponseByRollnumberAndSemester(String rollnumber, String semester);
    AchievementResponse getAchievementResponse(Integer id, UserPrincipal currentUser);
    AchievementResponse createAchievementResponse(AchievementRequest achievementRequest, UserPrincipal currentUser);
    AchievementResponse updateAchievementResponse(Integer id,AchievementRequest achievementRequest, UserPrincipal currentUser);
    PagedResponse<AchievementTemplateResponse> getAllAchievementTemplateResponse(int page, int size);
    AchievementTemplateResponse getAchievementTemplateResponse(Integer id, UserPrincipal currentUser);
    AchievementTemplateResponse createAchievementTemplateResponse(AchievementTemplateRequest achievementRequest, UserPrincipal currentUser);
    AchievementTemplateResponse updateAchievementTemplateResponse(Integer id,AchievementTemplateRequest achievementRequest, UserPrincipal currentUser);
}

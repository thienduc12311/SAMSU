package com.ftalk.samsu.controller.achievement;

import com.ftalk.samsu.model.achievement.Achievement;
import com.ftalk.samsu.model.achievement.AchievementTemplate;
import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.achievement.AchievementRequest;
import com.ftalk.samsu.payload.achievement.AchievementResponse;
import com.ftalk.samsu.payload.achievement.AchievementTemplateRequest;
import com.ftalk.samsu.payload.achievement.AchievementTemplateResponse;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerResponse;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionRequest;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionResponse;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.AchievementService;
import com.ftalk.samsu.service.FeedbackService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<PagedResponse<AchievementResponse>> getAllAchievement(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<AchievementResponse> response = achievementService.getAllAchievementResponse(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/rollnumber/{rollnumber}/semester/{semester}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<AchievementResponse>> getAllAchievement(
            @PathVariable(name = "eventId") String rollnumber,
            @PathVariable(name = "eventId") String semester) {
        List<AchievementResponse> response = achievementService.getAchievementResponseByRollnumberAndSemester(rollnumber, semester);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/templates")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<PagedResponse<AchievementTemplateResponse>> getAllAchievementTemplate(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<AchievementTemplateResponse> response = achievementService.getAllAchievementTemplateResponse(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<AchievementResponse> create(@Valid @RequestBody AchievementRequest achievementRequest,
                                                      @CurrentUser UserPrincipal currentUser) {
        AchievementResponse achievementResponse = achievementService.createAchievementResponse(achievementRequest, currentUser);
        return new ResponseEntity<>(achievementResponse, HttpStatus.CREATED);
    }

    @PostMapping("/template")
    public ResponseEntity<AchievementTemplateResponse> createTemplate(@Valid @RequestBody AchievementTemplateRequest achievementTemplateRequest,
                                                                      @CurrentUser UserPrincipal currentUser) {
        AchievementTemplateResponse achievementResponse = achievementService.createAchievementTemplateResponse(achievementTemplateRequest, currentUser);
        return new ResponseEntity<>(achievementResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{achievementId}")
    public ResponseEntity<AchievementResponse> get(
            @PathVariable(name = "achievementId") Integer id,
            @CurrentUser UserPrincipal currentUser) {
        AchievementResponse achievementResponse= achievementService.getAchievementResponse(id, currentUser);
        return new ResponseEntity<>(achievementResponse, HttpStatus.CREATED);
    }
    @GetMapping("/templates/{achievementTemplateId}")
    public ResponseEntity<AchievementTemplateResponse> getTemplate(
            @PathVariable(name = "achievementTemplateId") Integer id,
            @CurrentUser UserPrincipal currentUser) {
        AchievementTemplateResponse achievementResponse= achievementService.getAchievementTemplateResponse(id, currentUser);
        return new ResponseEntity<>(achievementResponse, HttpStatus.CREATED);
    }


}

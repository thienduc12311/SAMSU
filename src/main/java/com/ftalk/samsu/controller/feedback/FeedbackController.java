package com.ftalk.samsu.controller.feedback;

import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerResponse;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeCriteriaRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeCriteriaResponse;
import com.ftalk.samsu.repository.FeedbackAnswerRepository;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.FeedbackService;
import com.ftalk.samsu.service.GradePolicyService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/answers/questionId/{questionId}")
    public ResponseEntity<PagedResponse<FeedbackAnswerResponse>> getAllAnswerByQuestionId(
            @PathVariable(name = "questionId") Integer questionId,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<FeedbackAnswerResponse> response = feedbackService.getAllFeedbackAnswerByQuestionId(questionId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/answers")
    public ResponseEntity<FeedbackAnswerResponse> create(@Valid @RequestBody FeedbackAnswerRequest feedbackAnswerRequest,
                                                 @CurrentUser UserPrincipal currentUser) {
        FeedbackAnswer feedbackAnswer = feedbackService.submitFeedbackAnswer(feedbackAnswerRequest, currentUser);
        return new ResponseEntity<>(new FeedbackAnswerResponse(feedbackAnswer), HttpStatus.CREATED);
    }

    @GetMapping("/answers/{id}")
    public ResponseEntity<FeedbackAnswerResponse> getAnswer(@PathVariable(name = "id") Integer id, @CurrentUser UserPrincipal currentUser) {
        FeedbackAnswer feedbackAnswer = feedbackService.getFeedbackAnswer(id, currentUser);
        return new ResponseEntity<>(new FeedbackAnswerResponse(feedbackAnswer), HttpStatus.OK);
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<FeedbackQuestionResponse> getQuestion(@PathVariable(name = "id") Integer id, @CurrentUser UserPrincipal currentUser) {
        FeedbackQuestion feedbackQuestion = feedbackService.getFeedbackQuestion(id, currentUser);
        return new ResponseEntity<>(new FeedbackQuestionResponse(feedbackQuestion), HttpStatus.OK);
    }

}

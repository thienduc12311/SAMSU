package com.ftalk.samsu.controller.feedback;

import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerResponse;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionRequest;
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
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/answers/questionId/{questionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
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

    @PostMapping("/event/{eventId}/submit")
    public ResponseEntity<List<FeedbackAnswerResponse>> createList(
            @PathVariable(name = "eventId") Integer id,
            @Valid @RequestBody List<FeedbackAnswerRequest> feedbackAnswerRequests,
            @CurrentUser UserPrincipal currentUser) {
        List<FeedbackAnswerResponse> feedbackAnswers = feedbackService.submitFeedbackAnswer(id, feedbackAnswerRequests, currentUser);
        return new ResponseEntity<>(feedbackAnswers, HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventId}/questions")
    public ResponseEntity<List<FeedbackQuestionResponse>> get(
            @PathVariable(name = "eventId") Integer id,
            @CurrentUser UserPrincipal currentUser) {
        List<FeedbackQuestionResponse> feedbackQuestions = feedbackService.getFeedbackQuestions(id, currentUser);
        return new ResponseEntity<>(feedbackQuestions, HttpStatus.CREATED);
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

    @PostMapping("/questions/event/{eventId}")
    public ResponseEntity<FeedbackQuestionResponse> createQuestion(@PathVariable(name = "eventId") Integer eventId,
                                                                   @Valid @RequestBody FeedbackQuestionRequest feedbackQuestionRequest,
                                                                   @CurrentUser UserPrincipal currentUser) {
        FeedbackQuestionResponse feedbackQuestionResponse = feedbackService.addFeedbackQuestion(eventId, feedbackQuestionRequest);
        return new ResponseEntity<>(feedbackQuestionResponse, HttpStatus.OK);
    }

    @PutMapping("/questions/event/{eventId}")
    public ResponseEntity<FeedbackQuestionResponse> updateQuestion(@PathVariable(name = "eventId") Integer eventId,
                                                                   @Valid @RequestBody FeedbackQuestionRequest feedbackQuestionRequest,
                                                                   @CurrentUser UserPrincipal currentUser) {
        FeedbackQuestionResponse feedbackQuestionResponse = feedbackService.updateFeedbackQuestion(eventId, feedbackQuestionRequest, currentUser);
        return new ResponseEntity<>(feedbackQuestionResponse, HttpStatus.OK);
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse> updateQuestion(
            @PathVariable(name = "questionId") Integer questionId,
            @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = feedbackService.deleteFeedbackQuestion(questionId, currentUser);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}

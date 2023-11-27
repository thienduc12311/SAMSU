package com.ftalk.samsu.service;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerResponse;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionResponse;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;

public interface FeedbackService {

	PagedResponse<FeedbackAnswerResponse> getAllFeedbackAnswerByQuestionId(Integer questionId, int page, int size);

	FeedbackAnswer submitFeedbackAnswer(FeedbackAnswerRequest id, UserPrincipal currentUser);
	List<FeedbackAnswerResponse> submitFeedbackAnswer(Integer eventId, List<FeedbackAnswerRequest> feedbackAnswerRequests, UserPrincipal currentUser);

	FeedbackAnswer getFeedbackAnswer(Integer id, UserPrincipal currentUser);

	FeedbackQuestion getFeedbackQuestion(Integer id, UserPrincipal currentUser);

	List<FeedbackQuestionResponse> getFeedbackQuestions(Integer id, UserPrincipal currentUser);
}

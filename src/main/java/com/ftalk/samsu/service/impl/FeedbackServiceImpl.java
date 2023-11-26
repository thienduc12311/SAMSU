package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerResponse;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.FeedbackService;
import com.ftalk.samsu.service.PostService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.ftalk.samsu.utils.AppConstants.*;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackAnswerRepository feedbackAnswerRepository;

    @Autowired
    private FeedbackQuestionRepository feedbackQuestionRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public PagedResponse<FeedbackAnswerResponse> getAllFeedbackAnswerByQuestionId(Integer questionId, int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<FeedbackAnswer> feedbackAnswers = feedbackAnswerRepository.findAllByFeedbackQuestionId(questionId, pageable);

        List<FeedbackAnswer> content = feedbackAnswers.getNumberOfElements() == 0 ? Collections.emptyList() : feedbackAnswers.getContent();

        return new PagedResponse<>(ListConverter.listToList(content, FeedbackAnswerResponse::new), feedbackAnswers.getNumber(), feedbackAnswers.getSize(), feedbackAnswers.getTotalElements(),
                feedbackAnswers.getTotalPages(), feedbackAnswers.isLast());
    }

    @Override
    public FeedbackAnswer submitFeedbackAnswer(FeedbackAnswerRequest feedbackAnswerRequest, UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        FeedbackQuestion feedbackQuestion = feedbackQuestionRepository.findById(feedbackAnswerRequest.getQuestionId()).orElseThrow(() -> new ResourceNotFoundException("FeedbackQuestion", ID, feedbackAnswerRequest.getQuestionId()));
        FeedbackAnswer feedbackAnswer = new FeedbackAnswer(feedbackAnswerRequest);
        feedbackAnswer.setUser(user);
        feedbackAnswer.setFeedbackQuestion(feedbackQuestion);
        return feedbackAnswerRepository.save(feedbackAnswer);
    }

    @Override
    public FeedbackAnswer getFeedbackAnswer(Integer id, UserPrincipal currentUser) {
        return feedbackAnswerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FeedbackAnswers", ID, id));
    }

    @Override
    public FeedbackQuestion getFeedbackQuestion(Integer id, UserPrincipal currentUser) {
        return feedbackQuestionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FeedbackQuestions", ID, id));
    }
}

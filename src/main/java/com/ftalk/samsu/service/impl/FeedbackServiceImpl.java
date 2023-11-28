package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.model.participant.ParticipantId;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerRequest;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerResponse;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionResponse;
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

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ftalk.samsu.utils.AppConstants.*;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackAnswerRepository feedbackAnswerRepository;

    @Autowired
    private EventRepository eventRepository;


    @Autowired
    private FeedbackQuestionRepository feedbackQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;


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

    @Transactional
    @Override
    public List<FeedbackAnswerResponse> submitFeedbackAnswer(Integer eventId, List<FeedbackAnswerRequest> feedbackAnswerRequests, UserPrincipal currentUser) {
        Optional<Participant> participantOptional = participantRepository.findById(new ParticipantId(currentUser.getId(), eventId));
        if (!participantOptional.isPresent()) {
            throw new BadRequestException("You're not register this event");
        }
        if (participantOptional.get().getCheckout() != null) {
            throw new BadRequestException("You already submit feedback");
        }
        User user = userRepository.getUser(currentUser);
        Event event = eventRepository.getOne(eventId);
//        Date time = event.getStartTime();
//        long now = time.getTime() + event.getDuration();
//        if (now )
        List<FeedbackQuestion> feedbackQuestions = event.getFeedbackQuestions();
        Map<Integer, FeedbackQuestion> feedbackQuestionMap = feedbackQuestions.parallelStream().collect(Collectors.toMap(FeedbackQuestion::getId,
                Function.identity()));
        if (feedbackQuestions == null || feedbackQuestions.isEmpty()) {
            throw new BadRequestException("EventId don't have feedback form");
        }

        if (feedbackQuestions.size() != feedbackAnswerRequests.size()) {
            throw new BadRequestException("Question number of event not match feedback answers");
        }
        List<FeedbackAnswer> feedbackAnswers = feedbackAnswerRequests.parallelStream().map(feedbackAnswerRequest -> {
            FeedbackAnswer feedbackAnswer = new FeedbackAnswer(feedbackAnswerRequest);
            feedbackAnswer.setUser(user);
            feedbackAnswer.setFeedbackQuestion(feedbackQuestionMap.get(feedbackAnswerRequest.getQuestionId()));
            return feedbackAnswer;
        }).collect(Collectors.toList());

        participantOptional.get().setCheckout(new Date());
        participantRepository.save(participantOptional.get());
        user.setScore((short) (user.getScore() + event.getAttendScore()));
        userRepository.save(user);
        return ListConverter.listToList(feedbackAnswerRepository.saveAll(feedbackAnswers), FeedbackAnswerResponse::new);
    }

    @Override
    public FeedbackAnswer getFeedbackAnswer(Integer id, UserPrincipal currentUser) {
        return feedbackAnswerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FeedbackAnswers", ID, id));
    }

    @Override
    public FeedbackQuestion getFeedbackQuestion(Integer id, UserPrincipal currentUser) {
        return feedbackQuestionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("FeedbackQuestions", ID, id));
    }

    @Override
    public List<FeedbackQuestionResponse> getFeedbackQuestions(Integer id, UserPrincipal currentUser) {
        List<FeedbackQuestion> feedbackQuestionList = feedbackQuestionRepository.findAllByEventId(id);
        if (feedbackQuestionList == null || feedbackQuestionList.isEmpty()) {
            throw new BadRequestException("EventId don't have feedback form");
        }
        return ListConverter.listToList(feedbackQuestionList, FeedbackQuestionResponse::new);
    }
}

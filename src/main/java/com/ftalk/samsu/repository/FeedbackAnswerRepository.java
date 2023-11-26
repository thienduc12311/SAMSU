package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackAnswerRepository extends JpaRepository<FeedbackAnswer, Integer> {

	Page<FeedbackAnswer> findAllByFeedbackQuestionId(Integer feedbackQuestionId, Pageable pageable);

	Optional<FeedbackAnswer> findById(Integer id);

}

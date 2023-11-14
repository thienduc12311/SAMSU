package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackQuestionRepository extends JpaRepository<FeedbackQuestion, Integer> {

	Page<FeedbackQuestion> findAll(Pageable pageable);

	Optional<FeedbackQuestion> findById(Integer id);

}

package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

	Page<Task> findAll(Pageable pageable);

	Optional<Task> findTaskByEventIdAndTitle(Integer eventId, String tile);
	Optional<Task> findById(Integer id);

}

package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
	Page<Event> findById(Integer eventId, Pageable pageable);

	Page<Event> findAll(Pageable pageable);

	Page<Event> findAllByCreatorUserId(Integer id, Pageable pageable);


	Page<Event> findByCreatorUser(User user, Pageable pageable);

	Page<Event> findByStatus(Short status, Pageable pageable);

	Page<Event> findByDepartments(List<Department> departments, Pageable pageable);
	Page<Event> findByParticipantsRollnumber(String rollNumber, Pageable pageable);
	Page<Event> findBySemesterName(String semesterName, Pageable pageable);

	List<Event> findBySemesterName(String semesterName);

}

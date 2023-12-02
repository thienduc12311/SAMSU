package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.model.participant.ParticipantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssigneeRepository extends JpaRepository<Assignee, AssigneeId> {
    List<Assignee> findByIdTasksId(Integer taskId);

    List<Assignee> findAllByIdIn(List<AssigneeId> assigneeIds);

    Page<Assignee> findByIdUsersId(Integer taskId, Pageable pageable);

}


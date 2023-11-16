package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.AssigneeId;
import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.model.participant.ParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssigneeRepository extends JpaRepository<Assignee, AssigneeId> {
}

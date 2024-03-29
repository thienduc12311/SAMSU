package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.participant.Participant;
import com.ftalk.samsu.model.participant.ParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {
    List<Participant> findByParticipantId_EventsId(Integer eventId);

    List<Participant> findAllByParticipantIdIn(List<ParticipantId> participantIds);
    Integer countAllByParticipantIdUsersIdAndCheckinIsNotNullAndCheckoutIsNotNull(Integer id);
    List<Participant> findParticipantByParticipantIdEventsIdAndCheckinIsNotNullAndCheckoutIsNotNull(Integer id);
}


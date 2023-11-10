package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventProposalRepository extends JpaRepository<EventProposal, Integer> {
    Page<EventProposal> findAll(Pageable pageable);

    Page<EventProposal> findAllByCreatorUserId(User id, Pageable pageable);

    Optional<EventProposal> findById(Integer eventProposalId);
}
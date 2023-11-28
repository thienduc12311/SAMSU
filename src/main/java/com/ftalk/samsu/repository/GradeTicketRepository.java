package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeTicketRepository extends JpaRepository<GradeTicket, Integer> {
    Page<GradeTicket> findByGradeSubCriteriaId(Integer gradeSubCriteriaId, Pageable pageable);
}

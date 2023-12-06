package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeTicketRepository extends JpaRepository<GradeTicket, Integer> {
    Page<GradeTicket> findByGradeSubCriteriaId(Integer gradeSubCriteriaId, Pageable pageable);

    List<GradeTicket> findAllByCreatorUser_IdAndSemester_NameAndStatus(Integer uid, String semester, Short status);

    List<GradeTicket> findAllBySemester_NameAndStatus(String semester, Short status);
}

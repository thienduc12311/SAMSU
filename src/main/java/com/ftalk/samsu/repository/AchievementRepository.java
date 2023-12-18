package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.achievement.Achievement;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    List<Achievement> findAllByOwnerIdAndSemester(Integer id, String semester);
    Page<Achievement> findAchievementBySemesterName(String semester, Pageable pageable);
}

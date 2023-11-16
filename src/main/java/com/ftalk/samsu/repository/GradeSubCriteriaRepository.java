package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeSubCriteriaRepository extends JpaRepository<GradeSubCriteria, Integer> {
	Optional<GradeSubCriteria> findById(Integer id);

}

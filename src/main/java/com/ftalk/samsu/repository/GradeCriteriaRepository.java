package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeCriteriaRepository extends JpaRepository<GradeCriteria, Integer> {
	Optional<GradeCriteria> findById(Integer id);

}

package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.model.semester.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, String> {
	Optional<Semester> findByName(String name);
}

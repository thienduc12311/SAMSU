package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyDocumentRepository extends JpaRepository<PolicyDocument, Integer> {
	Optional<PolicyDocument> findById(Integer id);

//	Optional<PolicyDocument> findByName(String name);

	Boolean existsByName(String name);

//	List<Group> findAll();
}

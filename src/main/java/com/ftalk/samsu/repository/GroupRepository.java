package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.group.Group;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
	Optional<Group> findById(Integer userId);

	Optional<Group> findByName(String name);

	Boolean existsByName(String name);

	@EntityGraph(value = "Group.users", type = EntityGraph.EntityGraphType.LOAD)
	List<Group> findAll();
}

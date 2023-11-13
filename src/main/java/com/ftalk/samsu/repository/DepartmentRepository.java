package com.ftalk.samsu.repository;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.role.Role;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
	Optional<Department> findByName(String name);

	Optional<Department> findById(Integer id);

	Page<Department> findAll(Pageable pageable);
}

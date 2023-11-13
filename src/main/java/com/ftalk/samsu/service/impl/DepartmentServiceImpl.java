package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.Category;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.PostResponse;
import com.ftalk.samsu.repository.CategoryRepository;
import com.ftalk.samsu.repository.DepartmentRepository;
import com.ftalk.samsu.repository.PostRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.DepartmentService;
import com.ftalk.samsu.service.PostService;
import com.ftalk.samsu.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ftalk.samsu.utils.AppConstants.*;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public PagedResponse<Department> getAll(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, CREATED_AT);

        Page<Department> departments = departmentRepository.findAll(pageable);

        List<Department> content = departments.getNumberOfElements() == 0 ? Collections.emptyList() : departments.getContent();

        return new PagedResponse<>(content, departments.getNumber(), departments.getSize(), departments.getTotalElements(),
                departments.getTotalPages(), departments.isLast());
    }

    @Override
    public Department updateDepartment(Integer id, String departmentName, UserPrincipal currentUser) {
        Department department = departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department", ID, id));
        department.setName(departmentName);
        return departmentRepository.save(department);
    }

    @Override
    public ApiResponse deleteDepartment(Integer id, UserPrincipal currentUser) {
        departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department", ID, id));
        departmentRepository.deleteById(id);
        return new ApiResponse(Boolean.TRUE, "You successfully deleted post");
    }

    @Override
    public Department addDepartment(String departmentName, UserPrincipal currentUser) {
        Department department = new Department(departmentName);
        return departmentRepository.save(department);
    }

    @Override
    public Department getDepartment(Integer id) {
        return departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department", ID, id));
    }
}

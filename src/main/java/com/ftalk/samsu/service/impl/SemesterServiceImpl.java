package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.SemesterService;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.repository.SemesterRepository;
import com.ftalk.samsu.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public PagedResponse<Semester> getAllSemesters(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "name");

        Page<Semester> semesters = semesterRepository.findAll(pageable);

        List<Semester> content = semesters.getNumberOfElements() == 0 ? Collections.emptyList() : semesters.getContent();

        return new PagedResponse<>(content, semesters.getNumber(), semesters.getSize(), semesters.getTotalElements(), semesters.getTotalPages(), semesters.isLast());
    }

    @Override
    public Semester getSemester(String name) {
        return semesterRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Semester", "name", name));
    }

    @Override
    public Semester addSemester(Semester semester, UserPrincipal currentUser) {
        return semesterRepository.save(semester);
    }

    @Override
    public Semester updateSemester(String id, Semester newSemester, UserPrincipal currentUser) {
        Semester semester = semesterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));
        semester.setName(newSemester.getName());
        return semesterRepository.save(semester);
    }

    @Override
    public ApiResponse deleteSemester(String id, UserPrincipal currentUser) {
        Semester semester = semesterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));
        if (currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            semesterRepository.deleteById(id);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted semester");
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this semester");

        throw new UnauthorizedException(apiResponse);
    }
}























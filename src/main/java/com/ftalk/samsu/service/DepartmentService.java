package com.ftalk.samsu.service;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.PostResponse;
import com.ftalk.samsu.security.UserPrincipal;

public interface DepartmentService {

	PagedResponse<Department> getAll(int page, int size);

	Department updateDepartment(Integer id, String departmentName, UserPrincipal currentUser);

	ApiResponse deleteDepartment(Integer id, UserPrincipal currentUser);

	Department addDepartment(String departmentName, UserPrincipal currentUser);

	Department getDepartment(Integer id);

}

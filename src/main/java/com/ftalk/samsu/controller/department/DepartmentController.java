package com.ftalk.samsu.controller.department;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.PostRequest;
import com.ftalk.samsu.payload.PostResponse;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.DepartmentService;
import com.ftalk.samsu.service.PostService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
	@Autowired
	private DepartmentService departmentService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
	public ResponseEntity<PagedResponse<Department>> getAll(
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		PagedResponse<Department> response = departmentService.getAll(page, size);
		return new ResponseEntity< >(response, HttpStatus.OK);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
	public ResponseEntity<Department> addDepartment(@Valid @RequestBody String departmentName,
                                                @CurrentUser UserPrincipal currentUser) {
		Department department = departmentService.addDepartment(departmentName, currentUser);

		return new ResponseEntity< >(department, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Department> getDepartment(@PathVariable(name = "id") Integer id) {
		Department department = departmentService.getDepartment(id);
		return new ResponseEntity< >(department, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
	public ResponseEntity<Department> update(@PathVariable(name = "id") Integer id,
			@Valid @RequestBody String departmentName, @CurrentUser UserPrincipal currentUser) {
		Department department = departmentService.updateDepartment(id, departmentName, currentUser);
		return new ResponseEntity< >(department, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
	public ResponseEntity<ApiResponse> delete(@PathVariable(name = "id") Integer id, @CurrentUser UserPrincipal currentUser) {
		ApiResponse apiResponse = departmentService.deleteDepartment(id, currentUser);
		return new ResponseEntity< >(apiResponse, HttpStatus.OK);
	}
}

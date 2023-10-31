package com.ftalk.samsu.controller.semester;

import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.SemesterService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {
	@Autowired
	private SemesterService semesterService;

	@GetMapping
	public ResponseEntity<PagedResponse<Semester>> getAllSemesters(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

		PagedResponse<Semester> response = semesterService.getAllSemesters(page, size);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<Semester> addSemester(@Valid @RequestBody Semester semester, @CurrentUser UserPrincipal currentUser) {
		Semester newSemester = semesterService.addSemester(semester, currentUser);
		return new ResponseEntity< >(newSemester, HttpStatus.CREATED);
	}

	@GetMapping("/{name}")
	public ResponseEntity<Semester> getSemester(@PathVariable(name = "name") String name) {
		Semester semester = semesterService.getSemester(name);
		return new ResponseEntity< >(semester, HttpStatus.OK);
	}

	@PutMapping("/{name}")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<Semester> updateSemester(@PathVariable(name = "name") String name, @Valid @RequestBody Semester semester, @CurrentUser UserPrincipal currentUser) {

		Semester updatedSemester = semesterService.updateSemester(name, semester, currentUser);

		return new ResponseEntity< >(updatedSemester, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
	public ResponseEntity<ApiResponse> deleteSemester(@PathVariable(name = "id") String id, @CurrentUser UserPrincipal currentUser) {
		ApiResponse apiResponse = semesterService.deleteSemester(id, currentUser);

		return new ResponseEntity< >(apiResponse, HttpStatus.OK);
	}

}

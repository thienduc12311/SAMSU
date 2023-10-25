package com.ftalk.samsu.service;

import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.Tag;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;

public interface SemesterService {

	PagedResponse<Semester> getAllSemesters(int page, int size);

	Semester getSemester(String name);

	Semester addSemester(Semester semester, UserPrincipal currentUser);

	Semester updateSemester(String name, Semester newSemester, UserPrincipal currentUser);

	ApiResponse deleteSemester(String id, UserPrincipal currentUser);

}

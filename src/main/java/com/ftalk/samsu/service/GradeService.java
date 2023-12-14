package com.ftalk.samsu.service;

import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeAllEntryResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeAllResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeIndividualResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeResponse;
import com.ftalk.samsu.payload.group.GroupImportMemberResponse;
import com.ftalk.samsu.payload.group.GroupRequest;
import com.ftalk.samsu.security.UserPrincipal;

import java.util.List;
import java.util.Set;

public interface GradeService {

    GradeIndividualResponse getGradeHistory(String rollnumber, String semester, UserPrincipal current);

    GradeAllResponse getAllGrade(String semester, UserPrincipal currentUser);

//	List<GradeResponse> getGradeHistory(String rollnumber, String semester, UserPrincipal current);
}

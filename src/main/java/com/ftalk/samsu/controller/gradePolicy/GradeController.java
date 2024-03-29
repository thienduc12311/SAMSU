package com.ftalk.samsu.controller.gradePolicy;

import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.*;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GradeService;
import com.ftalk.samsu.service.GradeTicketService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade")
@PreAuthorize("isAuthenticated()")
public class GradeController {
    @Autowired
    private GradeService gradeService;

//    @GetMapping("/history/semester/{semesterName}")
//    public ResponseEntity<List<GradeResponse>> get(@PathVariable(name = "semesterName") String semester, @CurrentUser UserPrincipal currentUser) {
//        List<GradeResponse> gradeResponses = gradeService.getGradeHistory(currentUser.getRollnumber(), semester, currentUser);
//        return new ResponseEntity<>(gradeResponses, HttpStatus.OK);
//    }

    @GetMapping("/history/semester/{semesterName}/rollnumber/{rollnumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<GradeIndividualResponse> getByRollnumber(@PathVariable(name = "rollnumber") String rollnumber, @PathVariable(name = "semesterName") String semester, @CurrentUser UserPrincipal currentUser) {
        GradeIndividualResponse gradeResponses = gradeService.getGradeHistory(rollnumber, semester, currentUser);
        return new ResponseEntity<>(gradeResponses, HttpStatus.OK);
    }

    @GetMapping("/all/semester/{semesterName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<GradeAllResponse> getAll(@PathVariable(name = "semesterName") String semester, @CurrentUser UserPrincipal currentUser) {
        GradeAllResponse gradeResponses = gradeService.getAllGrade(semester, currentUser);
        return new ResponseEntity<>(gradeResponses, HttpStatus.OK);
    }


    @GetMapping("/history/semester/{semesterName}/me")
    public ResponseEntity<GradeIndividualResponse> getByRollnumber(@PathVariable(name = "semesterName") String semester, @CurrentUser UserPrincipal currentUser) {
        GradeIndividualResponse gradeResponses = gradeService.getGradeHistory(currentUser.getRollnumber(), semester, currentUser);
        return new ResponseEntity<>(gradeResponses, HttpStatus.OK);
    }


}
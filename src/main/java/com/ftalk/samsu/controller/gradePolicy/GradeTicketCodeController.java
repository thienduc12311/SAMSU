package com.ftalk.samsu.controller.gradePolicy;

import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketCreateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketUpdateRequest;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GradeTicketService;
import com.ftalk.samsu.utils.AppConstants;
import io.swagger.v3.oas.annotations.headers.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gradeTicketCode")
public class GradeTicketCodeController {
    @Autowired
    private GradeTicketService gradeTicketService;

    @GetMapping
    public ResponseEntity<GradeTicketResponse> get(@RequestHeader(name = "CodeTicket") String code) {
        GradeTicketResponse gradeTicket = gradeTicketService.getGradeTicketByCode(code);
        return new ResponseEntity<>(gradeTicket, HttpStatus.OK);
    }

    @PostMapping("/status/{status}/{code}")
    public ResponseEntity<ApiResponse> create(@PathVariable(name = "status") Short status,
                                              @RequestHeader(name = "CodeTicket") String code) {
        ApiResponse apiResponse = gradeTicketService.updateGradeTicketStatusByGuarantor(code, status);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

}

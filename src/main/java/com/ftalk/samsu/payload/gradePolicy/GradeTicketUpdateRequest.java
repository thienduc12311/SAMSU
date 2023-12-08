package com.ftalk.samsu.payload.gradePolicy;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeTicketUpdateRequest {
    private String title;
    private String content;
    private String evidenceUrls;
    private String feedback;
    private Integer gradeSubCriteriaId;
    private Short score;
    private Short status;
    private String guarantorEmail;
    private String semesterName;
    private Integer eventId;
}
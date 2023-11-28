package com.ftalk.samsu.payload.gradePolicy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeTicketCreateRequest {
    private String title;
    private String content;
    private String evidenceUrls;
    private String feedback;
}

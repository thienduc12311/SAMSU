package com.ftalk.samsu.payload.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeAllResponse {
    private List<GradeAllEntryResponse> gradeAllEntryResponses;
    private List<GradeSubCriteriaResponse> grade;
    private Map<Integer, GradeCriteriaResponse> mapGradeCriteriaResponse;
}
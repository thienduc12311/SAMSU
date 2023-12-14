package com.ftalk.samsu.payload.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeIndividualResponse {
    private Integer score;
    private List<GradeResponse> gradeHistory;
    private GradeAllResponse gradeAllResponse;

    public GradeIndividualResponse(List<GradeResponse> gradeHistory, GradeAllResponse gradeAllResponse) {
        this.gradeHistory = gradeHistory;
        this.gradeAllResponse = gradeAllResponse;
        calculate(gradeAllResponse);
    }

    private void calculate(GradeAllResponse gradeAllResponse) {
        Map<Integer, Short> mapScore = new ConcurrentHashMap<>();
        Map<Integer, Short> mapSubScore = new ConcurrentHashMap<>();
        Map<Integer, Short> maxScore = new ConcurrentHashMap<>();
        for (GradeCriteriaResponse gradeCriteria : gradeAllResponse.getGradeCriteriaResponses()) {
            mapScore.put(gradeCriteria.getId(), gradeCriteria.getDefaultScore());
            maxScore.put(gradeCriteria.getId(), gradeCriteria.getMaxScore());
        }
        if (gradeAllResponse.getStudentGrade().get(0).getScoreWithSubCriteria() != null) {
            for (Map.Entry<Integer, Short> entry : gradeAllResponse.getStudentGrade().get(0).getScoreWithSubCriteria().entrySet()) {
                mapSubScore.merge(entry.getKey(), entry.getValue(), (a, b) -> (short) (a + b));
            }
        }
        gradeAllResponse.getGradeSubCriteriaResponses().forEach(gradeSubCriteriaResponse -> {
            if (mapSubScore.get(gradeSubCriteriaResponse.getId()) != null) {
                mapScore.put(gradeSubCriteriaResponse.getGradeCriteriaId(),
                        min((short) (mapScore.get(gradeSubCriteriaResponse.getGradeCriteriaId()) + mapSubScore.get(gradeSubCriteriaResponse.getId())),
                                maxScore.get(gradeSubCriteriaResponse.getGradeCriteriaId())));
            }
        });
        AtomicInteger score = new AtomicInteger(0);
        mapScore.entrySet().forEach(integerShortEntry -> {
            score.addAndGet(integerShortEntry.getValue());
        });
        this.score = score.get();
    }

    private Short min(Short a, Short b) {
        if (a >= b) return b;
        else return a;
    }
}

package com.ftalk.samsu.payload.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
public class GradeSubCriteriaResponse {
    private Integer id;
    private String content;
    private Short minScore;
    private Short maxScore;
    private Integer gradeCriteriaId;

    public GradeSubCriteriaResponse(GradeSubCriteria gradeSubCriteria) {
        this.id = gradeSubCriteria.getId();
        this.content = gradeSubCriteria.getContent();
        this.minScore = gradeSubCriteria.getMinScore();
        this.maxScore = gradeSubCriteria.getMaxScore();
        this.gradeCriteriaId = gradeSubCriteria.getGradeCriteria() != null ? gradeSubCriteria.getGradeCriteria().getId() : null;
    }
}

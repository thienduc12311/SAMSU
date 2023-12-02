package com.ftalk.samsu.payload.gradePolicy;

import com.ftalk.samsu.model.gradePolicy.GradeCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.PolicyDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeCriteriaResponse implements Serializable {
    private static final long serialVersionUID = 27L;

    private Integer id;
    private String content;
    private Integer policyDocumentId;

    public GradeCriteriaResponse(GradeCriteria gradeCriteria) {
        this.id = gradeCriteria.getId();
        this.content = gradeCriteria.getContent();
        this.policyDocumentId = gradeCriteria.getPolicyDocument() != null ? gradeCriteria.getPolicyDocument().getId() : null;
    }
}

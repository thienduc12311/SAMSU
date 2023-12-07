package com.ftalk.samsu.model.gradePolicy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
@Table(name = "grade_criterias")
public class GradeCriteria implements Serializable {
    private static final long serialVersionUID = 12L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "default_score")
    private Short defaultScore;

    @NotNull
    @Column(name = "max_score")
    private Short maxScore;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_document_id")
    private PolicyDocument policyDocument;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "gradeCriteria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GradeSubCriteria> gradeSubCriteriaList;

    public GradeCriteria(String content, PolicyDocument policyDocument, Short defaultScore, Short maxScore) {
        this.content = content;
        this.policyDocument = policyDocument;
        this.defaultScore = defaultScore;
        this.maxScore = maxScore;
    }
}
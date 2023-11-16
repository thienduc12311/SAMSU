package com.ftalk.samsu.model.gradePolicy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
@Table(name = "grade_sub_criterias")
public class GradeSubCriteria implements Serializable {
    private static final long serialVersionUID = 11L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "content")
    private String content;


    @NotBlank
    @Column(name = "min_score")
    private Short minScore;

    @NotBlank
    @Column(name = "max_score")
    private Short maxScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_criterias_id")
    private GradeCriteria gradeCriteria;

    public GradeSubCriteria(String content, Short minScore, Short maxScore, GradeCriteria gradeCriteria) {
        this.content = content;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.gradeCriteria = gradeCriteria;
    }
}

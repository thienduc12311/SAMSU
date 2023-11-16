package com.ftalk.samsu.model.gradePolicy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_document_id")
    private PolicyDocument policyDocument;

    public GradeCriteria(String content, PolicyDocument policyDocument) {
        this.content = content;
        this.policyDocument = policyDocument;
    }
}
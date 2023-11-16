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
@Table(name = "policy_documents")
public class PolicyDocument implements Serializable {
    private static final long serialVersionUID = 13L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "fileUrls")
    private String fileUrls;

    public PolicyDocument(String name, String fileUrls) {
        this.name = name;
        this.fileUrls = fileUrls;
    }
}

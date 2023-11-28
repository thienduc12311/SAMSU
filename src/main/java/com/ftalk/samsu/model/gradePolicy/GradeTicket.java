package com.ftalk.samsu.model.gradePolicy;

import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grade_ticket")
public class GradeTicket extends DateAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "title")
    @Size(max = 1000)
    private String title;

    @Column(name = "content")
    @Size(max = 5000)
    private String content;

    @Column(name = "evidence_urls")
    @Size(max = 2000)
    private String evidenceUrls;

    @Column(name = "feedback")
    @Size(max = 5000)
    private String feedback;

    @Column(name = "status", insertable = false)
    private Short status;

    @Column(name = "score")
    private Integer score;

    @ManyToOne
    @JoinColumn(name = "creator_users_id")
    private User creatorUser;

    @ManyToOne
    @JoinColumn(name = "accepter_users_id")
    private User accepterUser;

    @ManyToOne
    @JoinColumn(name = "grade_sub_criterias_id")
    private GradeSubCriteria gradeSubCriteria;

    public GradeTicket(String title, String content, String evidenceUrls, String feedback, User creatorUser) {
        this.title = title;
        this.content = content;
        this.evidenceUrls = evidenceUrls;
        this.feedback = feedback;
        this.creatorUser = creatorUser;
    }


}
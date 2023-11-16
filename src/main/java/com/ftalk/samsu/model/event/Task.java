package com.ftalk.samsu.model.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.event.TaskRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task extends DateAudit implements Serializable {
    private static final long serialVersionUID = 14L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "title")
    @Size(max = 1000)
    private String title;

    @NotBlank
    @Column(name = "content")
    @Size(max = 5000)
    private String content;

    @NotNull
    @Column(name = "status")
    private Short status;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "creator_users_id")
    private User creatorUserId;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_id")
    private Event event;

    @NotNull
    @Column(name = "score")
    private Short score;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gradeSubCriteria_id")
    private GradeSubCriteria gradeSubCriteria;

//    @JsonIgnore
//    @EqualsAndHashCode.Exclude
//    @ManyToMany(fetch = FetchType.LAZY, cascade = {
//            CascadeType.PERSIST,
//            CascadeType.MERGE
//    })
//    @JoinTable(name = "assignees",
//            joinColumns = {@JoinColumn(name = "tasks_id")},
//            inverseJoinColumns = {@JoinColumn(name = "users_id")})
//    private Set<User> assignees;

    public Task(TaskRequest taskRequest) {
        this.title = taskRequest.getTitle();
        this.content = taskRequest.getContent();
        this.status = taskRequest.getStatus();
        this.score = taskRequest.getScore();
        this.setCreatedAt(new Date());
    }

}

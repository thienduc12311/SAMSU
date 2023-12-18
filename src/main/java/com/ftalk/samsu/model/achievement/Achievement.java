package com.ftalk.samsu.model.achievement;

import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.semester.Semester;
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
@Table(name = "achievements")
public class Achievement extends DateAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "title")
    @Size(max = 1000)
    private String title;

    @Column(name = "content")
    @Size(max = 2000)
    private String content;

    @Column(name = "url")
    @Size(max = 1000)
    private String url;

    @ManyToOne
    @JoinColumn(name = "achivement_templates_id")
    private AchievementTemplate achievementTemplate;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "semesters_name")
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "onwer_users_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "creator_users_id")
    private User creator;

    public Achievement(String title, String content, String url) {
        this.title = title;
        this.content = content;
        this.url = url;
    }
}
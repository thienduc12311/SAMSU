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

@EqualsAndHashCode
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "achievement_templates")
public class AchievementTemplate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    @Size(max = 1000)
    private String title;

    @Column(name = "content")
    @Size(max = 9000)
    private String content;

    public AchievementTemplate(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
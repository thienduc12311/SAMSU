package com.ftalk.samsu.model.announcement;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "announcements")
public class Announcement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "type")
    private Short type;

    @NotNull
    @Column(name = "title")
    @Size(max = 255)
    private String title;

    @Column(name = "content")
    @Size(max = 5000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "creator_users_id")
    private User creatorUser;

    public Announcement(Short type, String title, String content, User creatorUser) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.creatorUser = creatorUser;
    }
}

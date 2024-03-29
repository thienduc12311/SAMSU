package com.ftalk.samsu.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.group.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})})
public class User extends DateAudit {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    @Size(max = 1000)
    private String name;

    @NotBlank
    @Column(name = "username")
    @Size(max = 45)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 255)
    @Column(name = "password")
    private String password;

    @NotBlank
    @NaturalId
    @Size(max = 45)
    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "group_members",
            joinColumns = {@JoinColumn(name = "users_id")},
            inverseJoinColumns = {@JoinColumn(name = "groups_id")})
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<Group> groups;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "departments_id")
    private Department department;

    @Column(name = "avatar")
    @Size(max = 1000)
    private String avatar;

    @Column(name = "score")
    private Short score;

    @Column(name = "dob")
    private Date dob;

//	@Column(name = "created_at")
//	private Date created_at;

    @NotNull
    @Column(name = "role")
    private Short role;

    @NotNull
    @Column(name = "status")
    private Short status;

    @NotNull
    @Column(name = "rollnumber")
    private String rollnumber;

    public User(String username, String password, String email, String name, String rollnumber, Short role, Short status, Short score) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
        this.name = name;
        this.score = score == null ? (short) 60 : score;
        this.rollnumber = rollnumber;
    }

    public User(String username, String password, String email, String name, String rollnumber, Short role, Short status, String avatar, Date dob, Short score) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
        this.name = name;
        this.rollnumber = rollnumber;
        this.avatar = avatar;
        this.dob = dob;
        this.score = score == null ? (short) 60 : score;
    }

    public User(Integer id) {
        this.id = id;
    }

}

package com.ftalk.samsu.model.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
@Table(name = "group_users", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@NamedEntityGraph(name = "Group.users", attributeNodes = @NamedAttributeNode("users"))
public class Group implements Serializable {
    private static final long serialVersionUID = 12L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "group_members",
            joinColumns = {@JoinColumn(name = "groups_id")},
            inverseJoinColumns = {@JoinColumn(name = "users_id")})
    private Set<User> users;

    public Group(String name, Set<User> users) {
        this.name = name;
        this.users = users;
    }
}

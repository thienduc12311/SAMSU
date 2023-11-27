package com.ftalk.samsu.payload.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftalk.samsu.model.group.Group;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserUpdate {
    private String name;

    @NotBlank
    @Size(max = 45)
    private String username;

    @Size(max = 255)
    private String password;

    @NotBlank
    @Size(max = 45)
    private String email;

    private Integer departmentId;

    @Size(max = 1000)
    private String avatar;

    private Date dob;

    @NotNull
    private String role;

    private Short status;

    @NotNull
    private String rollnumber;

    public UserUpdate(String name, String username, String password, String email, Integer departmentId, String avatar, Date dob, String role, Short status, String rollnumber) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.departmentId = departmentId;
        this.avatar = avatar;
        this.dob = dob;
        this.role = role;
        this.status = status;
        this.rollnumber = rollnumber;
    }
}

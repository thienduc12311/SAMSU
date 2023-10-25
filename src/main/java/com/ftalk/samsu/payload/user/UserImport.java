package com.ftalk.samsu.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UserImport {
    private String username;

    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String rollnumber;

    @NotBlank
    private String email;

    @NotNull
    private String role;

    public User createUser(Short status) {
        return new User(username, password, email, name, rollnumber, UserRole.getRoleValue(role),  status);
    }
}

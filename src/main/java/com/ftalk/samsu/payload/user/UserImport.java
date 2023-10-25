package com.ftalk.samsu.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Validated
@NoArgsConstructor
public class UserImport {
    private String username;
    private String password;

    @JsonProperty("name")
    @NotBlank
    private String name;

    @JsonProperty("rollnumber")
    @NotBlank
    private String rollnumber;

    @JsonProperty("email")
    @NotBlank
    private String email;

    @JsonProperty("role")
    @NotNull
    private String role;

    public User createUser(Short status) {
        return new User(username, password, email, name, rollnumber, UserRole.getRoleValue(role),  status);
    }
}

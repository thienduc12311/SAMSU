package com.ftalk.samsu.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Validated
public class UserImport {
    private String username;

    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String rollnumber;

    @NotBlank
    private String email;

    private String role;

    public User createUser(Short status) {
        return new User(username, password, email, name, rollnumber, UserRole.getRoleValue(role),  status);
    }

    public boolean isValid(){
        return !StringUtils.isEmpty(username) && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(rollnumber) && !StringUtils.isEmpty(email);
    }
}

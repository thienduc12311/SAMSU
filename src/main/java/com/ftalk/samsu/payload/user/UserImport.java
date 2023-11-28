package com.ftalk.samsu.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import com.ftalk.samsu.utils.AppConstants;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UserImport {
    @NotBlank
    private String username;

    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String rollnumber;

    @NotBlank
    private String email;

    private String role;
    private String avatar;
    private Date dob;

    public User createUser(Short status) {
        short roleValue = UserRole.getRoleValue(role);
        short score = UserRole.ROLE_STUDENT == roleValue ? AppConstants.DEFAULT_SCORE : (short) 0;
        return new User(username, password, email, name, rollnumber, roleValue, status, avatar, dob, score);
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(username) && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(rollnumber) && !StringUtils.isEmpty(email);
    }
}

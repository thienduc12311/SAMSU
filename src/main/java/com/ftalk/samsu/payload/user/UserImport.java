package com.ftalk.samsu.payload.user;

import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
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
        return new User(username, password, email, name, rollnumber, UserRole.getRoleValue(role), new Date(), status);
    }
}

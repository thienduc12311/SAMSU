package com.ftalk.samsu.payload.user;

import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import com.ftalk.samsu.utils.user.PasswordValidator;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
public class UserPasswordRequest {
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    public boolean isValid(){
        return PasswordValidator.isPasswordValid(newPassword);
    }
}

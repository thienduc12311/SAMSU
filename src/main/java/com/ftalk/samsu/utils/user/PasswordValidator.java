package com.ftalk.samsu.utils.user;

import com.ftalk.samsu.exception.BadRequestException;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

public class PasswordValidator {
    public static boolean isPasswordValid(String password) {
        if (StringUtils.isEmpty(password)){
            return false;
        }
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return  false;
        }

        return true;
    }

}

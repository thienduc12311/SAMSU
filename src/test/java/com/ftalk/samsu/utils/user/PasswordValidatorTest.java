package com.ftalk.samsu.utils.user;

import com.ftalk.samsu.exception.BadRequestException;
import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordValidatorTest {

    @Test
    public void isPasswordValidWithLength() {
        assert !PasswordValidator.isPasswordValid("Abc1");
    }

    @Test
    public void isPasswordValidWithSpecialCharacter() {
        assert !PasswordValidator.isPasswordValid("Abc12311");
    }

    @Test
    public void isPasswordValidWithUppercase() {
        assert !PasswordValidator.isPasswordValid("abc1234$");
    }
}
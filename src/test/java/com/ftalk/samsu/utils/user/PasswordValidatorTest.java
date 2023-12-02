package com.ftalk.samsu.utils.user;

import com.ftalk.samsu.exception.BadRequestException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;

import static org.junit.Assert.*;

public class PasswordValidatorTest {


    @Test
    public void isPasswordValidWithLength() {
        assert !PasswordValidator.isPasswordValid("Abc1");
    }

    @CacheEvict(value = {"eventsCache"}, allEntries = true)
    @Test
    public void isPasswordValidWithSpecialCharacter() {
        assert !PasswordValidator.isPasswordValid("Abc12311");
    }

    @Test
    public void isPasswordValidWithUppercase() {
        assert !PasswordValidator.isPasswordValid("abc1234$");
    }

}
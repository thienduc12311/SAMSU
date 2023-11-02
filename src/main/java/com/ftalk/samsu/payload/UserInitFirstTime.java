package com.ftalk.samsu.payload;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class UserInitFirstTime {
    @NotBlank
    private String password;
}

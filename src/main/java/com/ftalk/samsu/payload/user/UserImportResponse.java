package com.ftalk.samsu.payload.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserImportResponse {
    private int amount;
    private int success;
    private int failed;
    private List<UserImportFailed> userImportsFail;
}

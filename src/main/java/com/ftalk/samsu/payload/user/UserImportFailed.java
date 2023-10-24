package com.ftalk.samsu.payload.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserImportFailed {
    private UserImport userImport;
    private String message;
}

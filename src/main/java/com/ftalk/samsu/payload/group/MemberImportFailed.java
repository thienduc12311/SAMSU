package com.ftalk.samsu.payload.group;

import com.ftalk.samsu.payload.user.UserImport;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberImportFailed {
    private String rollnumber;
    private String message;
}

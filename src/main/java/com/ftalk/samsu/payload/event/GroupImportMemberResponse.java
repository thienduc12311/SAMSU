package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.payload.user.UserImportFailed;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GroupImportMemberResponse {
    private int amount;
    private int success;
    private int failed;
    private List<MemberImportFailed> memberImportsFail;
}

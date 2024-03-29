package com.ftalk.samsu.payload.group;

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

package com.ftalk.samsu.payload.group;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class GroupRequest {
    @NotBlank
    @Size(max = 500)
    private String name;
    private Set<Integer> userIds;
}

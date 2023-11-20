package com.ftalk.samsu.payload.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssigneeRequest {
    private String rollnumber;
    private Short status;
}

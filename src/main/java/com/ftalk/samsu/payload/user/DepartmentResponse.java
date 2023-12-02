package com.ftalk.samsu.payload.user;

import com.ftalk.samsu.model.user.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse {
    private Integer id;
    private String name;

    public DepartmentResponse(Department department) {
        this.id = department.getId();
        this.name = department.getName();
    }
}

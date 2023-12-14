package com.ftalk.samsu.payload.user;

import com.ftalk.samsu.model.user.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse implements Serializable {
    private static final long serialVersionUID = 27L;
    private Integer id;
    private String name;

    public DepartmentResponse(Department department) {
        this.id = department.getId();
        this.name = department.getName();
    }
}

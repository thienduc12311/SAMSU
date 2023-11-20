package com.ftalk.samsu.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssigneeId implements Serializable {
    @Column(name = "tasks_id")
    private Integer tasksId;
    @Column(name = "users_id")
    private Integer usersId;
}

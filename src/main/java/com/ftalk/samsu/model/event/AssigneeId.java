package com.ftalk.samsu.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssigneeId implements Serializable {
    private Integer tasks_id;
    private Integer users_id;
}

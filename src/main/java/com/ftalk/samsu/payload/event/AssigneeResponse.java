package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssigneeResponse implements Serializable {
    private static final long serialVersionUID = 26L;

    private UserProfileReduce user;
    private TaskResponse task;
    private Short status;
    private Date createAt;

    public AssigneeResponse(Assignee assignee) {
        this.user = new UserProfileReduce(assignee.getAssignee());
        this.task = new TaskResponse(assignee.getTask());
        this.status = assignee.getStatus();
        this.createAt = assignee.getCreatedAt();
    }
}

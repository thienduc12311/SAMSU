package com.ftalk.samsu.payload.achievement;

import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.payload.event.TaskResponse;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskListRequest {
    private List<UserProfileReduce> assignee;
    private TaskInAchievement task;
}

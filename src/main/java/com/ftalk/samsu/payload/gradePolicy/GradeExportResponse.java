package com.ftalk.samsu.payload.gradePolicy;

import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeExportResponse {
    private Short score;
    private Short type;
    private String title;
    private Integer id;
    private Date time;

    public GradeExportResponse(Event event, Date time){
        this.score = event.getAttendScore();
        this.type = (short) 1;
        this.title = event.getTitle();
        this.id = event.getId();
        this.time = time;
    }

    public GradeExportResponse(Task task, Date time){
        this.score = task.getScore();
        this.type = (short) 2;
        this.title = task.getTitle();
        this.id = task.getId();
        this.time = time;
    }

    public GradeExportResponse(GradeTicket gradeTicket, Date time){
        this.score = gradeTicket.getScore();
        this.type = (short) 2;
        this.title = gradeTicket.getTitle();
        this.id = gradeTicket.getId();
        this.time = time;
    }

}

package com.ftalk.samsu.payload.gradePolicy;

import com.ftalk.samsu.model.event.Assignee;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.event.Task;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.model.participant.Participant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeResponse {
    private Short score;
    //0: ticket grade ; 1: participants grade; 2: task grade;
    private Short type;
    private String title;
    private Integer id;
    private Date time;
    private Integer gradeSubCriteriaId;

    public GradeResponse(Event event, Date time){
        this.score = event.getAttendScore() != null ? event.getAttendScore() : 0;
        this.type = (short) 0;
        this.title = event.getTitle();
        this.id = event.getId();
        this.time = time;
        this.gradeSubCriteriaId =event.getAttendGradeSubCriteria() != null ? event.getAttendGradeSubCriteria().getId() : null;
    }

    public GradeResponse(Task task, Date time){
        this.score = task.getScore();
        this.type = (short) 1;
        this.title = task.getTitle();
        this.id = task.getId();
        this.time = time;
        this.gradeSubCriteriaId = task.getGradeSubCriteria().getId();
    }

    public GradeResponse(GradeTicket gradeTicket, Date time){
        this.score = gradeTicket.getScore();
        this.type = (short) 2;
        this.title = gradeTicket.getTitle();
        this.id = gradeTicket.getId();
        this.time = time;
        this.gradeSubCriteriaId = gradeTicket.getGradeSubCriteria().getId();
    }

}

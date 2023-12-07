package com.ftalk.samsu.payload.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackQuestionResponse implements Serializable {
    private static final long serialVersionUID = -2792369707368779346L;

    private Integer id;
    private Short type;
    private String question;
    private String answer;
    private Integer eventId;

    public FeedbackQuestionResponse(FeedbackQuestion feedbackQuestion) {
        this.id = feedbackQuestion.getId();
        this.type = feedbackQuestion.getType();
        this.question = feedbackQuestion.getQuestion();
        this.answer = feedbackQuestion.getAnswer();
        this.eventId = feedbackQuestion.getEvent().getId();
    }
}

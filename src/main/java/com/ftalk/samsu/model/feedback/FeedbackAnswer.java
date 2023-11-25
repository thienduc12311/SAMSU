package com.ftalk.samsu.model.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.feedback.FeedbackAnswerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feedback_questions")
public class FeedbackAnswer extends DateAudit implements Serializable {
    private static final long serialVersionUID = 14L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content")
    @Size(max = 5000)
    private String content;


    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_questions_id")
    private FeedbackQuestion feedbackQuestion;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_users_id")
    private User user;

    public FeedbackAnswer(FeedbackAnswerRequest feedbackAnswerRequest) {
        this.content = feedbackAnswerRequest.getContent();
        this.setCreatedAt(new Date());
    }
}
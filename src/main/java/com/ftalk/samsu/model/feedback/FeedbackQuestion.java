package com.ftalk.samsu.model.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.event.EventProposalRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

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
public class FeedbackQuestion extends DateAudit implements Serializable {
    private static final long serialVersionUID = 14L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "type")
    private Short type;

    @NotBlank
    @Column(name = "question")
    @Size(max = 2000)
    private String question;

    @NotBlank
    @Column(name = "answer")
    @Size(max = 2000)
    private String answer;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public FeedbackQuestion(Short type, String question, String answer) {
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.setCreatedAt(new Date());
    }
}
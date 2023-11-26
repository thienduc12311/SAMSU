package com.ftalk.samsu.payload.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.feedback.FeedbackAnswer;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackAnswerResponse {
    private Integer id;
    private String content;
    private Integer feedbackQuestionId;
    private UserProfileReduce userProfileReduce;

    public FeedbackAnswerResponse(FeedbackAnswer feedbackAnswer) {
        this.id = feedbackAnswer.getId();
        this.content = feedbackAnswer.getContent();
        this.feedbackQuestionId = feedbackAnswer.getFeedbackQuestion().getId();
        this.userProfileReduce = new UserProfileReduce(feedbackAnswer.getUser());
    }
}

package com.ftalk.samsu.payload.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackAnswerRequest {
    @NotNull
    private Integer questionId;

    @Size(max = 5000)
    private String content;

}

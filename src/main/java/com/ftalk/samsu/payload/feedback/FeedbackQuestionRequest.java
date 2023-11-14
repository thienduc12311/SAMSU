package com.ftalk.samsu.payload.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackQuestionRequest {
    @NotNull
    private Short type;
    @NotBlank
    @Size(max = 2000)
    private String question;

    @Size(max = 2000)
    private String answer;
}

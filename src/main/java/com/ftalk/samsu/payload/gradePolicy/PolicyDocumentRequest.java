package com.ftalk.samsu.payload.gradePolicy;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class PolicyDocumentRequest {
    private String name;
    private String fileUrls;
}

package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.feedback.FeedbackQuestionRequest;
import com.ftalk.samsu.utils.event.EventUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateRequest {
    @NotNull
    private Short status;

    @NotNull
    private Integer duration;

    @NotNull
    private Short attendScore;

    @NotNull
    @Size(max = 1000)
    private String title;

    @NotNull
    @Size(max = 5000)
    private String content;

    @NotNull
    private Integer eventProposalId;

    @NotNull
    private String eventLeaderRollnumber;

    @NotNull
    private String semester;

    private String bannerUrl;

    private String fileUrls;

    @NotNull
    private Date startTime;

    private List<FeedbackQuestionRequest> feedbackQuestionRequestList;

    private Set<Integer> departmentIds;

    private Set<String> rollnumbers;

    public void validate(){
        if (!StringUtils.isEmpty(fileUrls) && !EventUtils.validateFileUrlsS3(fileUrls)){
            throw new BadRequestException("FileUrls not valid");
        }

        if (!StringUtils.isEmpty(bannerUrl) && !EventUtils.validateUrlS3(bannerUrl)){
            throw new BadRequestException("FileUrls not valid");
        }
    }
}

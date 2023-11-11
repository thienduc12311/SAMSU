package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.utils.event.EventProposalConstants;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Data
public class EventProposalResponse {
    private Integer id;
    private String title;
    private String content;
    private String feedback;
    private String status;
    private String creatorRollnumber;
    private String accepterRollnumber;
    private String fileUrls;
    private Date modifyAt;

    public EventProposalResponse(EventProposal eventProposal){
        id = eventProposal.getId();
        title = eventProposal.getTitle();
        content = eventProposal.getContent();
        feedback = eventProposal.getFeedback();
        status = Objects.requireNonNull(EventProposalConstants.findByValue(eventProposal.getStatus())).name();
        creatorRollnumber = eventProposal.getCreatorUserId().getRollnumber();
        if (eventProposal.getAccepterUserId() != null) {
            accepterRollnumber = eventProposal.getAccepterUserId().getRollnumber();
        }
        fileUrls = eventProposal.getFileUrls();
        modifyAt = eventProposal.getModifyAt();
    }
}

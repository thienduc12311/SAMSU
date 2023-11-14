package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import com.ftalk.samsu.utils.event.EventProposalConstants;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class EventProposalResponse {
    private Integer id;
    private String title;
    private String content;
    private String feedback;
    private String status;
    private UserProfileReduce creator;
    private UserProfileReduce accepter;
    private String fileUrls;
    private Date modifyAt;
    private Date createAt;

    public EventProposalResponse(EventProposal eventProposal){
        id = eventProposal.getId();
        title = eventProposal.getTitle();
        content = eventProposal.getContent();
        feedback = eventProposal.getFeedback();
        status = Objects.requireNonNull(EventProposalConstants.findByValue(eventProposal.getStatus())).name();
        creator = new UserProfileReduce(eventProposal.getCreatorUserId().getUsername(), eventProposal.getCreatorUserId().getAvatar());
        if (eventProposal.getAccepterUserId() != null) {
            accepter = new UserProfileReduce(eventProposal.getAccepterUserId().getUsername(), eventProposal.getAccepterUserId().getAvatar());
        }
        fileUrls = eventProposal.getFileUrls();
        modifyAt = eventProposal.getModifyAt();
        createAt = eventProposal.getCreatedAt();
    }


}

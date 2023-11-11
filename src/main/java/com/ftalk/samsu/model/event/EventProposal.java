package com.ftalk.samsu.model.event;

import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.event.EventProposalRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Table(name = "event_proposals")
public class EventProposal extends DateAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Column(name = "title")
	@Size(max = 1000)
	private String title;

	@NotBlank
	@Column(name = "content")
	@Size(max = 5000)
	private String content;

	@Column(name = "feedback")
	@Size(max = 2000)
	private String feedback;

	@NotNull
	@Column(name = "status")
	private Short status;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "creator_users_id")
	private User creatorUserId;

	@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY,  orphanRemoval = true)
	@JoinColumn(name = "accepter_users_id")
	private User accepterUserId;

	@NotBlank
	@Column(name = "file_urls") // Explicitly map to the database column
	@Size(max = 5000)
	private String fileUrls;

	@LastModifiedDate
	private Date modifyAt;

	public EventProposal(String title, String content, Short status, User creatorUserId, String fileUrls) {
		this.title = title;
		this.content = content;
		this.status = status;
		this.creatorUserId = creatorUserId;
		this.fileUrls = fileUrls;
	}

//	public EventProposal(EventProposalRequest eventProposalRequest, Short status, User creatorUserId) {
//		this.title = eventProposalRequest.getTitle();
//		this.content =  eventProposalRequest.getContent();
//		this.status = status;
//		this.creatorUserId = creatorUserId ;
//		this.fileUrls = eventProposalRequest.getFileUrls();
//	}

	public EventProposal(EventProposalRequest eventProposalRequest, Short status, User creatorUserId, boolean createDate) {
		this.title = eventProposalRequest.getTitle();
		this.content =  eventProposalRequest.getContent();
		this.status = status;
		this.creatorUserId = creatorUserId ;
		this.fileUrls = eventProposalRequest.getFileUrls();
		if (createDate) this.setCreatedAt(new Date());
//		if (modifyDate) this.modifyAt = new Date();
	}
}

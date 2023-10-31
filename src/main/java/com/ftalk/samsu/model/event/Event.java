package com.ftalk.samsu.model.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.Photo;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@Table(name = "events", uniqueConstraints = { @UniqueConstraint(columnNames = { "title" }) })
public class Event extends DateAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Column(name = "status")
	@Size(max = 10)
	private Short status;

	@NotNull
	@Column(name = "duration")
	private Integer duration;

	@NotNull
	@Column(name = "title")
	@Size(max = 1000)
	private String title;

	@NotNull
	@Column(name = "content")
	@Size(max = 5000)
	private String content;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "creator_user_id")
	private User creatorUserId;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_proposal_id")
	private User eventProposalId;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_leader_users_id")
	private User eventLeaderUserId;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "semesters_name")
	private User semestersName;

	@Column(name = "banner_url")
	@Size(max = 1000)
	private String bannerUrl;

	@Column(name = "fileUrls")
	@Size(max = 2000)
	private String fileUrls;

	@Column(name = "start_time")
	private Date start_time;

}

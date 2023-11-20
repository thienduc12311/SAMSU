package com.ftalk.samsu.model.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.Photo;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
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
import java.util.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events", uniqueConstraints = { @UniqueConstraint(columnNames = { "title" }) })
public class Event extends DateAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Column(name = "status")
	private Short status;

	@NotNull
	@Column(name = "duration")
	private Integer duration;

	@NotNull
	@Column(name = "attend_score")
	private Short attendScore;

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
	private User creatorUser;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_proposal_id")
	private EventProposal eventProposal;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_leader_users_id")
	private User eventLeaderUser;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "semesters_name")
	private Semester semester;

	@NotBlank
	@Column(name = "banner_url")
	@Size(max = 1000)
	private String bannerUrl;

	@Column(name = "fileUrls")
	@Size(max = 2000)
	private String fileUrls;

	@NotNull
	@Column(name = "start_time")
	private Date startTime;

	@ManyToMany(fetch = FetchType.LAZY, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
	})
	@JoinTable(name = "department_collaborators",
			joinColumns = {@JoinColumn(name = "events_id")},
			inverseJoinColumns = {@JoinColumn(name = "departments_id")})
	private List<Department> departments;

	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@ManyToMany(fetch = FetchType.LAZY, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
	})
	@JoinTable(name = "participants",
			joinColumns = {@JoinColumn(name = "events_id")},
			inverseJoinColumns = {@JoinColumn(name = "users_id")})
	private Set<User> participants;

	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
	private List<FeedbackQuestion> feedbackQuestions;

	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", cascade = CascadeType.ALL)
	private List<Post> posts;

	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
	private List<Task> tasks;

	public Event(Short status, Integer duration, String title, String content, User creatorUser, Short attendScore,
				 EventProposal eventProposal, User eventLeaderUser, Semester semester, String bannerUrl, String fileUrls, Date startTime) {
		this.status = status;
		this.duration = duration;
		this.title = title;
		this.content = content;
		this.attendScore = attendScore;
		this.creatorUser = creatorUser;
		this.eventProposal = eventProposal;
		this.eventLeaderUser = eventLeaderUser;
		this.semester = semester;
		this.bannerUrl = bannerUrl;
		this.fileUrls = fileUrls;
		this.startTime = startTime;
	}
}

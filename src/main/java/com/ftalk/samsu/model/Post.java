package com.ftalk.samsu.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ftalk.samsu.model.audit.UserDateAudit;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.PostRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "posts")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Post extends UserDateAudit {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@Column(name = "title")
	private String title;

	@NotNull
	@Column(name = "body")
	private String body;

	@Column(name = "kudos")
	private Integer kudos ;

	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "events_id")
	private Event event;

	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_users_id")
	private User user;

	@Column(name = "image_urls")
	private String image_urls ;

	@Column(name = "file_urls")
	private String file_urls ;

	@Column(name = "status")
	private Short status ;

	public Post(PostRequest postRequest) {
		this.title = postRequest.getTitle();
		this.body = postRequest.getBody();
		this.kudos = postRequest.getKudos() != null ? postRequest.getKudos() : 0;
		this.image_urls = postRequest.getImage_urls();
		this.file_urls = postRequest.getFile_urls();
		this.status = postRequest.getStatus();
	}
}

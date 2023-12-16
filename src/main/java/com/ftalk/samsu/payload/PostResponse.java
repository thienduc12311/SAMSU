package com.ftalk.samsu.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.event.Event;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.event.EventResponse;
import com.ftalk.samsu.payload.user.UserProfileReduce;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class PostResponse {
	private Integer id;
	private String title;
	private String body;
	private Integer kudos ;
	private EventResponse event;
	private UserProfileReduce user;
	private String image_urls ;
	private String file_urls ;
	private Short status ;

	private Date createdAt;

	public PostResponse(Post post) {
		this.id = post.getId();
		this.title = post.getTitle();
		this.body = post.getBody();
		this.kudos = post.getKudos();
		this.event = post.getEvent() != null ? new EventResponse(post.getEvent()) : null;
		this.user = post.getUser() != null ? new UserProfileReduce(post.getUser()) : null;
		this.image_urls = post.getImage_urls();
		this.file_urls = post.getFile_urls();
		this.status = post.getStatus();
		this.createdAt = post.getCreatedAt();
	}
}
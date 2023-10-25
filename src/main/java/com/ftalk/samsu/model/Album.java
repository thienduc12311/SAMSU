package com.ftalk.samsu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.audit.UserDateAudit;
import com.ftalk.samsu.model.user.User;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
@Entity
@Data
@Table(name = "events", uniqueConstraints = { @UniqueConstraint(columnNames = { "title" }) })
public class Album  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(name = "title")
	private String title;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Photo> photo;

	@JsonIgnore
	public User getUser() {
		return user;
	}

	public List<Photo> getPhoto() {
		return this.photo == null ? null : new ArrayList<>(this.photo);
	}

	public void setPhoto(List<Photo> photo) {
		if (photo == null) {
			this.photo = null;
		} else {
			this.photo = Collections.unmodifiableList(photo);
		}
	}
}

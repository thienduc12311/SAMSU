package com.ftalk.samsu.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.Album;
import com.ftalk.samsu.model.Comment;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.role.Role;
import com.ftalk.samsu.model.Todo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
		@UniqueConstraint(columnNames = { "email" }) })
public class User extends DateAudit {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@NotBlank
	@Column(name = "name")
	@Size(max = 1000)
	private String name;

	@NotBlank
	@Column(name = "username")
	@Size(max = 45)
	private String username;

	@NotBlank
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Size(max = 255)
	@Column(name = "password")
	private String password;

	@NotBlank
	@NaturalId
	@Size(max = 45)
	@Column(name = "email")
	@Email
	private String email;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "departments_id")
	private Department department;

	@Column(name = "avatar")
	@Size(max = 1000)
	private String avatar;

	@Column(name = "dob")
	private Date dob;

	@NotNull
	@Column(name = "role")
	private Short role;

	@NotNull
	@Column(name = "status")
	private Short status;


	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

}

package com.ftalk.samsu.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.audit.UserDateAudit;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
@Table(name = "departments")
public class Department implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank
	@Column(name = "name")
	private String name;

	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "department")
	private List<User> users;
//test
	public Department(String name) {
		this.name = name;
	}

}

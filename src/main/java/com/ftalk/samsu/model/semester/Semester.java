package com.ftalk.samsu.model.semester;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.audit.UserDateAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
@Table(name = "semesters")
public class Semester implements Serializable {

	private static final long serialVersionUID = -5298707266367331514L;

	@NotBlank
	@Id
	@Column(name = "name")
	private String name;

	public Semester(String name) {
		super();
		this.name = name;
	}

}

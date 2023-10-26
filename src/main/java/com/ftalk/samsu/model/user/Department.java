package com.ftalk.samsu.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.audit.UserDateAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@EqualsAndHashCode
@Entity
@Data
@NoArgsConstructor
@Table(name = "departments")
public class Department {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name")
	private String name;

	public Department(String name) {
		this.name = name;
	}

	@JsonIgnore
	public Integer getId() {
		return id;
	}
}

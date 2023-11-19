package com.ftalk.samsu.model.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.audit.DateAudit;
import com.ftalk.samsu.model.feedback.FeedbackQuestion;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.Department;
import com.ftalk.samsu.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@Table(name = "assignees")
public class Assignee extends DateAudit implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AssigneeId id;

	@NotNull
	@Column(name = "status")
	private Short status;

	public Assignee(AssigneeId assigneeId, Short status) {
		this.id = assigneeId;
		this.status = status;
	}



}

package com.ftalk.samsu.payload.event;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EventProposalEvaluateRequest {

	@Size(max = 2000)
	private String feedback;

	@NotNull
	@Size(max = 10)
	private Short status;

}

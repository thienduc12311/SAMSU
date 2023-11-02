package com.ftalk.samsu.payload.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class EventProposalEvaluateRequest {

	@Size(max = 2000)
	private String feedback;

	@NotNull
	@Size(max = 10)
	private Short status;

}

package com.ftalk.samsu.payload.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventProposalEvaluateRequest {

	@Size(max = 2000)
	private String feedback;

	@NotNull
	private String status;

}

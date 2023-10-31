package com.ftalk.samsu.payload.event;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EventProposalUpdateRequest {

	@NotNull
	@Size(max = 1000)
	private String title;

	@NotNull
	@Size(max = 5000)
	private String content;

	@Size(max = 2000)
	private String feedback;

	@NotNull
	private String creatorUserRollnumber;

	private String accepterUserRollnumber;

	@NotNull
	@Size(max = 2000)
	private String fileUrls;

}

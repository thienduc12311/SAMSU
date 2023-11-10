package com.ftalk.samsu.payload.event;

import com.ftalk.samsu.model.event.EventProposal;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class EventProposalRequest {

	@NotBlank(message = "Title cannot be empty")
	@Size(max = 1000)
	private String title;

	@NotBlank(message = "Content cannot be empty")
	@Size(max = 5000)
	private String content;

	@NotBlank(message = "Attach file cannot be empty")
	@Size(max = 2000)
	private String fileUrls;

}

package com.ftalk.samsu.payload.user;

import com.ftalk.samsu.payload.DateAuditPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class UserDateAuditPayload extends DateAuditPayload {
	private Long createdBy;

	private Long updatedBy;

}

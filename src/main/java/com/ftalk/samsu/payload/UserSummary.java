package com.ftalk.samsu.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSummary {
	private Integer id;
	private String username;
}

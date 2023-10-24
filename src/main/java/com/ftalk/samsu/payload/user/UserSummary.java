package com.ftalk.samsu.payload.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserSummary {
	private String name;
	private String email;
	private String avatar;
	private Date created_at;
	private Date dob;
	private String rollnumber;
}

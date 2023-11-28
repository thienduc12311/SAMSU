package com.ftalk.samsu.payload.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
	private String username;
	private String rollnumber;
	private String name;
	private String role;
	private String status;
	private Date dob;
	private String department;
	private Short score;
}

package com.ftalk.samsu.payload.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileReduce {
	private String username;
	private String avatar;
	private String rollnumber;
}

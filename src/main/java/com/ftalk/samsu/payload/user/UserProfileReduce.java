package com.ftalk.samsu.payload.user;

import com.ftalk.samsu.model.user.User;
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

	public UserProfileReduce(User user) {
		this.username = user.getName();
		this.avatar = user.getAvatar();
		this.rollnumber = user.getRollnumber();
	}
}

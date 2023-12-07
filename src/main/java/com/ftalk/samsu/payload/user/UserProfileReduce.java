package com.ftalk.samsu.payload.user;

import com.ftalk.samsu.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileReduce implements Serializable {
	private static final long serialVersionUID = 7750766013133765139L;
	private String name;
	private String username;
	private String avatar;
	private String rollnumber;

	public UserProfileReduce(User user) {
		this.name = user.getName();
		this.username = user.getUsername();
		this.avatar = user.getAvatar();
		this.rollnumber = user.getRollnumber();
	}
}
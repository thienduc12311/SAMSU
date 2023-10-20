package com.ftalk.samsu.payload.user;

import com.ftalk.samsu.model.user.Address;
import com.ftalk.samsu.model.user.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
	private Integer id;
	private String username;
	private Instant joinedAt;
	private String email;
	private Long postCount;
}

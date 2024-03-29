package com.ftalk.samsu.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.model.user.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Getter
	private Integer id;
	private final String username;

	@Getter
	@JsonIgnore
	private String email;

	@JsonIgnore
	private String password;

	@Getter
	private final String rollnumber;

	private final Collection<? extends GrantedAuthority> authorities;

	public UserPrincipal(Integer id, String username, String email, String password, String rollnumber,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.rollnumber = rollnumber;
		if (authorities == null) {
			this.authorities = null;
		} else {
			this.authorities = new ArrayList<>(authorities);
		}
	}

	public static UserPrincipal create(User user) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(UserRole.getRole(user.getRole())));
		return new UserPrincipal(user.getId(), user.getUsername(),
				user.getEmail(), user.getPassword(),user.getRollnumber(),authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities == null ? null : new ArrayList<>(authorities);
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || getClass() != object.getClass())
			return false;
		UserPrincipal that = (UserPrincipal) object;
		return Objects.equals(id, that.id);
	}

	public int hashCode() {
		return Objects.hash(id);
	}
}

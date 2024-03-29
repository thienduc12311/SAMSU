package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService, CustomUserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) {
		User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with this username or email: %s", usernameOrEmail)));
		if (user.getStatus() != 1) return null;
		return UserPrincipal.create(user);
	}

	@Override
	@Transactional
	public UserDetails loadUserById(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with id: %s", id)));
		if (user.getStatus() != 1) return null;
		return UserPrincipal.create(user);
	}
}

package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.*;
import com.ftalk.samsu.payload.*;
import com.ftalk.samsu.payload.user.UserIdentityAvailability;
import com.ftalk.samsu.payload.user.UserProfile;
import com.ftalk.samsu.payload.user.UserSummary;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.role.Role;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.user.Address;
import com.ftalk.samsu.model.user.Company;
import com.ftalk.samsu.model.user.Geo;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.repository.PostRepository;
import com.ftalk.samsu.repository.RoleRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserSummary getCurrentUser(UserPrincipal currentUser) {
		return new UserSummary(currentUser.getId(), currentUser.getUsername());
	}

	@Override
	public UserIdentityAvailability checkUsernameAvailability(String username) {
		Boolean isAvailable = !userRepository.existsByUsername(username);
		return new UserIdentityAvailability(isAvailable);
	}

	@Override
	public UserIdentityAvailability checkEmailAvailability(String email) {
		Boolean isAvailable = !userRepository.existsByEmail(email);
		return new UserIdentityAvailability(isAvailable);
	}

	@Override
	public UserProfile getUserProfile(String username) {
		User user = userRepository.getUserByName(username);

		Long postCount = postRepository.countByCreatedBy(user.getId());

		return new UserProfile(user.getId(), user.getUsername(),
				user.getCreatedAt(), user.getEmail(), postCount);
	}

	@Override
	public User addUser(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Username is already taken");
			throw new BadRequestException(apiResponse);
		}

		if (userRepository.existsByEmail(user.getEmail())) {
			ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Email is already taken");
			throw new BadRequestException(apiResponse);
		}

		List<Role> roles = new ArrayList<>();
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
//		user.setRoles(roles);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public User updateUser(User newUser, String username, UserPrincipal currentUser) {
		User user = userRepository.getUserByName(username);
		if (!checkUsernameAvailability(newUser.getUsername()).getAvailable()){
			throw new BadRequestException("Username not available");
		}
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			user.setPassword(passwordEncoder.encode(newUser.getPassword()));
			return userRepository.save(user);
		}
		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update profile of: " + username);
		throw new UnauthorizedException(apiResponse);

	}

	@Override
	public User updateUser(User newUser, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with jwt token: %s", currentUser.getEmail())));
		if (!checkUsernameAvailability(newUser.getUsername()).getAvailable()){
			throw new BadRequestException("Username not available");
		}
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			user.setUsername(newUser.getUsername());
			user.setPassword(passwordEncoder.encode(newUser.getPassword()));
			return userRepository.save(user);
		}
		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update profile of: " + currentUser.getEmail());
		throw new UnauthorizedException(apiResponse);
	}

	@Override
	public User initAccount(UserInitFirstTime newUser, UserPrincipal currentUser) {
		User user = userRepository.findById(currentUser.getId())
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with jwt token: %s", currentUser.getEmail())));
		if (!checkUsernameAvailability(newUser.getUsername()).getAvailable()){
			throw new BadRequestException("Username not available");
		}
		if (!StringUtils.isEmpty(user.getUsername())){
			throw new BadRequestException("This account already init! If you want change information please update ");
		}
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			user.setUsername(newUser.getUsername());
			user.setPassword(passwordEncoder.encode(newUser.getPassword()));
			return userRepository.save(user);
		}
		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update profile of: " + currentUser.getEmail());
		throw new UnauthorizedException(apiResponse);
	}

	@Override
	public ApiResponse deleteUser(String username, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
		if (!user.getId().equals(currentUser.getId()) || !currentUser.getAuthorities()
				.contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete profile of: " + username);
			throw new AccessDeniedException(apiResponse);
		}
//		userRepository.deleteById(user.getId());

		return new ApiResponse(Boolean.TRUE, "You successfully deleted profile of: " + username);
	}

	@Override
	public ApiResponse giveAdmin(String username) {
		User user = userRepository.getUserByName(username);
		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
				.orElseThrow(() -> new AppException("User role not set")));
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
//		user.setRoles(roles);
		userRepository.save(user);
		return new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + username);
	}

	@Override
	public ApiResponse removeAdmin(String username) {
		User user = userRepository.getUserByName(username);
		List<Role> roles = new ArrayList<>();
		roles.add(
				roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
//		user.setRoles(roles);
		userRepository.save(user);
		return new ApiResponse(Boolean.TRUE, "You took ADMIN role from user: " + username);
	}

	@Override
	public UserProfile setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest) {
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		Geo geo = new Geo(infoRequest.getLat(), infoRequest.getLng());
		Address address = new Address(infoRequest.getStreet(), infoRequest.getSuite(), infoRequest.getCity(),
				infoRequest.getZipcode(), geo);
		Company company = new Company(infoRequest.getCompanyName(), infoRequest.getCatchPhrase(), infoRequest.getBs());
		if (user.getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
//			user.setAddress(address);
//			user.setCompany(company);
//			user.setWebsite(infoRequest.getWebsite());
//			user.setPhone(infoRequest.getPhone());
			User updatedUser = userRepository.save(user);

			Long postCount = postRepository.countByCreatedBy(updatedUser.getId());

			return new UserProfile(updatedUser.getId(), updatedUser.getUsername(),
					 updatedUser.getCreatedAt(),
					updatedUser.getEmail(), postCount);
		}

		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update users profile", HttpStatus.FORBIDDEN);
		throw new AccessDeniedException(apiResponse);
	}
}

package com.ftalk.samsu.service;

import com.ftalk.samsu.payload.*;
import com.ftalk.samsu.payload.user.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.user.User;

import java.util.List;

public interface UserService {

	UserProfile getCurrentUser(UserPrincipal currentUser);

	UserIdentityAvailability checkUsernameAvailability(String username);

	UserIdentityAvailability checkEmailAvailability(String email);

	UserProfile getUserProfile(String username);

	User addUser(User user);

	void updatePassword(UserPasswordRequest userPasswordRequest, UserPrincipal currentUser);

	UserImportResponse addListUser(List<UserImport> user);

	PagedResponse<User> getAllUserIn(Integer page, Integer size);

	User updateUser(User newUser, String rollnumber, UserPrincipal currentUser);

	User updateUser(User newUser, UserPrincipal currentUser);

	User initAccount(UserInitFirstTime newUser, UserPrincipal currentUser);

	ApiResponse deleteUser(String username, UserPrincipal currentUser);

}
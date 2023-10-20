package com.ftalk.samsu.service;

import com.ftalk.samsu.payload.*;
import com.ftalk.samsu.payload.user.UserIdentityAvailability;
import com.ftalk.samsu.payload.user.UserProfile;
import com.ftalk.samsu.payload.user.UserSummary;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.user.User;

public interface UserService {

	UserSummary getCurrentUser(UserPrincipal currentUser);

	UserIdentityAvailability checkUsernameAvailability(String username);

	UserIdentityAvailability checkEmailAvailability(String email);

	UserProfile getUserProfile(String username);

	User addUser(User user);

	User updateUser(User newUser, String username, UserPrincipal currentUser);

	User updateUser(User newUser, UserPrincipal currentUser);

	User initAccount(UserInitFirstTime newUser, UserPrincipal currentUser);

	ApiResponse deleteUser(String username, UserPrincipal currentUser);

	ApiResponse giveAdmin(String username);

	ApiResponse removeAdmin(String username);

	UserProfile setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest);

}
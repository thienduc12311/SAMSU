package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.*;
import com.ftalk.samsu.model.Album;
import com.ftalk.samsu.model.user.*;
import com.ftalk.samsu.payload.*;
import com.ftalk.samsu.payload.user.*;
import com.ftalk.samsu.security.JwtAuthenticationEntryPoint;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.model.role.Role;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.repository.PostRepository;
import com.ftalk.samsu.repository.RoleRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.service.UserService;
import com.ftalk.samsu.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final String FIND_KEY = "id";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String SECRET_CHECK_TOKEN = "SAMSU_123456";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserProfile getCurrentUser(UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with jwt token: %s", currentUser.getEmail())));
        return new UserProfile(user.getUsername(), user.getRollnumber(),user.getName(), UserRole.getRole(user.getRole()), UserStatus.getStatus(user.getStatus()), user.getDob(), user.getDepartment() != null ? user.getDepartment().getName() : null);
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
        return null;
    }

    @Override
    public User addUser(User user) {
        if ( userRepository.existsByRollnumber(user.getRollnumber())) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Rollnumber is already taken");
            throw new BadRequestException(apiResponse);
        }
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
    public void updatePassword(UserPasswordRequest userPasswordRequest, UserPrincipal currentUser) {
        if (!userPasswordRequest.isValid()){
            throw new BadRequestException("Password must be at least 8 characters long, 1 special characters and 1 uppercase letter");
        }
        int rs = userRepository.updatePasswordById(userPasswordRequest.getOldPassword(), userPasswordRequest.getNewPassword(), currentUser.getId());
        if (rs < 1){
            throw new SamsuApiException(HttpStatus.INTERNAL_SERVER_ERROR,"Can't update password with jwt token, please contact with admin!");
        }
    }

    @Override
    public UserImportResponse addListUser(List<UserImport> userImportList) {
        List<User> importList = new ArrayList<>();
        List<UserImportFailed> importListFailed = new ArrayList<>();
        for (UserImport userImport : userImportList) {
            try {
                UserImportFailed userImportFailed = checkValid(userImport);
                if (userImportFailed != null){
                    importListFailed.add(userImportFailed);
                    continue;
                }
                importList.add(userImport.createUser((short) 1));
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                importListFailed.add(new UserImportFailed(userImport, ex.getMessage()));
            }
        }
        try {
            userRepository.saveAll(importList);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(),ex);
            throw new BadRequestException(new ApiResponse(Boolean.FALSE, "Import list user failed with DataIntegrityViolationException"));
        }
        return new UserImportResponse(userImportList.size(),importList.size(),importListFailed.size(),importListFailed);
    }

    @Override
    public PagedResponse<User> getAllUserIn(Integer page, Integer size){
        AppUtils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, FIND_KEY);
        Page<User> users = userRepository.findAll(pageable);
        if (users.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), users.getNumber(), users.getSize(), users.getTotalElements(),
                    users.getTotalPages(), users.isLast());
        }

//        List<UserResponse> userResponses = Arrays.asList(modelMapper.map(users.getContent(), UserResponse[].class));

        return new PagedResponse<>(users.getContent(), users.getNumber(), users.getSize(), users.getTotalElements(), users.getTotalPages(),
                users.isLast());
    }

    private UserImportFailed checkValid(UserImport userImport){
        if (!userImport.isValid()){
            return new UserImportFailed(userImport, "Have required field null");
        }
        if (userRepository.existsByUsername(userImport.getUsername())) {
            return new UserImportFailed(userImport, "Username is already taken");
        }
        if (userRepository.existsByEmail(userImport.getUsername())) {
            return new UserImportFailed(userImport, "Email is already taken");
        }
        if ( userRepository.existsByRollnumber(userImport.getRollnumber())) {
            return new UserImportFailed(userImport, "Rollnumber is already taken");
        }
        return null;
    }

    @Override
    public User updateUser(User newUser, String rollnumber, UserPrincipal currentUser) {
        User user = userRepository.getUserByRollnumber(rollnumber);
        if (!checkUsernameAvailability(newUser.getUsername()).getAvailable()) {
            throw new BadRequestException("Username not available");
        }
        if (user.getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            return userRepository.save(user);
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update profile of: " + rollnumber);
        throw new UnauthorizedException(apiResponse);

    }

    @Override
    public User updateUser(User newUser, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found with jwt token: %s", currentUser.getEmail())));
        if (!checkUsernameAvailability(newUser.getUsername()).getAvailable()) {
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
        if (!StringUtils.isEmpty(user.getPassword())) {
            throw new BadRequestException("This account already init! If you want change information please update ");
        }
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
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
    public boolean validateToken(String secret, UserPrincipal currentUser) {
        return SECRET_CHECK_TOKEN.equals(secret);
    }

//    @Override
//    public ApiResponse giveAdmin(String username) {
//        User user = userRepository.getUserByName(username);
//        List<Role> roles = new ArrayList<>();
//        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
//                .orElseThrow(() -> new AppException("User role not set")));
//        roles.add(
//                roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
////		user.setRoles(roles);
//        userRepository.save(user);
//        return new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + username);
//    }
//
//    @Override
//    public ApiResponse removeAdmin(String username) {
//        User user = userRepository.getUserByName(username);
//        List<Role> roles = new ArrayList<>();
//        roles.add(
//                roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
////		user.setRoles(roles);
//        userRepository.save(user);
//        return new ApiResponse(Boolean.TRUE, "You took ADMIN role from user: " + username);
//    }


//	@Override
//	public UserProfile setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest) {
//		User user = userRepository.findByUsername(currentUser.getUsername())
//				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
//		Geo geo = new Geo(infoRequest.getLat(), infoRequest.getLng());
//		Address address = new Address(infoRequest.getStreet(), infoRequest.getSuite(), infoRequest.getCity(),
//				infoRequest.getZipcode(), geo);
//		Company company = new Company(infoRequest.getCompanyName(), infoRequest.getCatchPhrase(), infoRequest.getBs());
//		if (user.getId().equals(currentUser.getId())
//				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
////			user.setAddress(address);
////			user.setCompany(company);
////			user.setWebsite(infoRequest.getWebsite());
////			user.setPhone(infoRequest.getPhone());
//			User updatedUser = userRepository.save(user);
//
//			Long postCount = postRepository.countByCreatedBy(updatedUser.getId());
//
//			return new UserProfile(updatedUser.getId(), updatedUser.getUsername(),
//					 updatedUser.getCreatedAt(),
//					updatedUser.getEmail(), postCount);
//		}
//
//		ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update users profile", HttpStatus.FORBIDDEN);
//		throw new AccessDeniedException(apiResponse);
//	}
}

package com.ftalk.samsu.controller.user;

import com.ftalk.samsu.model.Post;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.*;
import com.ftalk.samsu.payload.user.*;
import com.ftalk.samsu.security.CurrentUser;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.AlbumService;
import com.ftalk.samsu.service.PostService;
import com.ftalk.samsu.service.UserService;
import com.ftalk.samsu.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserProfile userProfile = userService.getCurrentUser(currentUser);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    @GetMapping("/checkUsernameAvailability")
    public ResponseEntity<UserIdentityAvailability> checkUsernameAvailability(@RequestParam(value = "username") String username) {
        UserIdentityAvailability userIdentityAvailability = userService.checkUsernameAvailability(username);

        return new ResponseEntity<>(userIdentityAvailability, HttpStatus.OK);
    }

    @GetMapping("/checkEmailAvailability")
    public ResponseEntity<UserIdentityAvailability> checkEmailAvailability(@RequestParam(value = "email") String email) {
        UserIdentityAvailability userIdentityAvailability = userService.checkEmailAvailability(email);
        return new ResponseEntity<>(userIdentityAvailability, HttpStatus.OK);
    }

    @GetMapping("/{rollnumber}/profile")
    public ResponseEntity<UserProfile> getUSerProfile(@PathVariable(value = "rollnumber") String rollnumber,@CurrentUser UserPrincipal currentUser) {
        UserProfile userProfile = userService.getUserProfile(rollnumber,currentUser);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    @GetMapping("/{rollnumber}/posts")
    public ResponseEntity<PagedResponse<Post>> getPostsCreatedBy(@PathVariable(value = "username") String rollnumber,
                                                                 @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                 @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<Post> response = postService.getPostsByCreatedBy(rollnumber, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<User>> getUsersCreatedBy(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<User> response = userService.getAllUserIn(page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        User newUser = userService.addUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<UserImportResponse> addListUser(@Valid @RequestBody List<UserImport> userImports) {
        UserImportResponse userImportResponse = userService.addListUser(userImports);
        return new ResponseEntity<>(userImportResponse, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{rollnumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User newUser,
                                           @PathVariable(value = "rollnumber") String rollnumber, @CurrentUser UserPrincipal currentUser) {
        User updatedUSer = userService.updateUser(newUser, rollnumber, currentUser);

        return new ResponseEntity<>(updatedUSer, HttpStatus.ACCEPTED);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<User> updateUser(@Valid @RequestBody String secret,
                                           @CurrentUser UserPrincipal currentUser) {
        if (userService.validateToken(secret, currentUser)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    @DeleteMapping("/{rollnumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable(value = "rollnumber") String username,
                                                  @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = userService.deleteUser(username, currentUser);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/init")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UserInitFirstTime newUser,
                                              @CurrentUser UserPrincipal currentUser) {
        User updatedUSer = userService.initAccount(newUser, currentUser);
        return new ResponseEntity<>(updatedUSer, HttpStatus.CREATED);
    }

    @PutMapping("/passwords")
    public ResponseEntity<User> updatePassword(@Valid @RequestBody UserPasswordRequest userPasswordRequest,
                                               @CurrentUser UserPrincipal currentUser) {
        userService.updatePassword(userPasswordRequest, currentUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

package com.ftalk.samsu.controller;

import com.ftalk.samsu.exception.SamsuApiException;
import com.ftalk.samsu.payload.login.JwtAuthenticationResponse;
import com.ftalk.samsu.payload.login.LoginGoogleResponse;
import com.ftalk.samsu.payload.login.LoginRequest;
import com.ftalk.samsu.repository.RoleRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.JwtTokenProvider;
import com.ftalk.samsu.service.impl.CustomUserDetailsServiceImpl;
import com.ftalk.samsu.utils.google.GooglePojo;
import com.ftalk.samsu.utils.google.GoogleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String USER_ROLE_NOT_SET = "User role not set";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private GoogleUtils googleUtils;

    @Autowired
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @RequestMapping("/callback/login-google")
    public ResponseEntity<LoginGoogleResponse> loginGoogleCallback(HttpServletRequest request, @RequestParam("code") String code) throws ClientProtocolException, IOException {
        if (code == null || code.isEmpty()) {
            throw new SamsuApiException(HttpStatus.FORBIDDEN, "Sorry, You're not authorized to access this resource.");
        }
        String accessToken = googleUtils.getToken(code);
        return signInByGoogleWithAccessToken(request, accessToken);
    }

    @RequestMapping("/login-google")
    public ResponseEntity<LoginGoogleResponse> loginGoogle(HttpServletRequest request, @RequestParam("accessToken") String accessToken) throws ClientProtocolException, IOException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new SamsuApiException(HttpStatus.FORBIDDEN, "Sorry, You're not authorized to access this resource.");
        }
        return signInByGoogleWithAccessToken(request, accessToken);
    }

    private ResponseEntity<LoginGoogleResponse> signInByGoogleWithAccessToken(HttpServletRequest request, String accessToken) throws ClientProtocolException, IOException {
        GooglePojo googlePojo = googleUtils.getUserInfo(accessToken);
        if (Boolean.FALSE.equals(userRepository.existsByEmail(googlePojo.getEmail()))) {
            throw new SamsuApiException(HttpStatus.FORBIDDEN, "Sorry, You're not authorized to access this resource.");
        }
        UserDetails userDetail = customUserDetailsService.loadUserByUsername(googlePojo.getEmail());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new LoginGoogleResponse(new JwtAuthenticationResponse(jwt)
                , googlePojo.getEmail(), StringUtils.isEmpty(userDetail.getPassword())));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

//    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
//        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
//            throw new SamsuApiException(HttpStatus.BAD_REQUEST, "Username is already taken");
//        }
//        if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
//            throw new SamsuApiException(HttpStatus.BAD_REQUEST, "Email is already taken");
//        }
//        String username = signUpRequest.getUsername().toLowerCase();
//        String email = signUpRequest.getEmail().toLowerCase();
//        String password = passwordEncoder.encode(signUpRequest.getPassword());
//        User user = new User(username, email, password);
//
//        List<Role> roles = new ArrayList<>();
//
//        if (userRepository.count() == 0) {
//            roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
//            roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
//        } else {
//            roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
//        }
//
//        User result = userRepository.save(user);
//
//        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}").buildAndExpand(result.getId()).toUri();
//
//        return ResponseEntity.created(location).body(new ApiResponse(Boolean.TRUE, "User registered successfully"));
//    }
}

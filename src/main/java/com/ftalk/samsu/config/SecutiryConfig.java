package com.ftalk.samsu.config;

import com.ftalk.samsu.security.JwtAuthenticationEntryPoint;
import com.ftalk.samsu.security.JwtAuthenticationFilter;
import com.ftalk.samsu.service.impl.CustomUserDetailsServiceImpl;
import com.ftalk.samsu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		securedEnabled = true,
		jsr250Enabled = true,
		prePostEnabled = true)
public class SecutiryConfig extends WebSecurityConfigurerAdapter {
	private final CustomUserDetailsServiceImpl customUserDetailsService;
	private final JwtAuthenticationEntryPoint unauthorizedHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	public SecutiryConfig(UserRepository userRepository, CustomUserDetailsServiceImpl customUserDetailsService,
			JwtAuthenticationEntryPoint unauthorizedHandler, JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.customUserDetailsService = customUserDetailsService;
		this.unauthorizedHandler = unauthorizedHandler;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors()
				.and()
				.csrf().disable()
				.exceptionHandling()
				.authenticationEntryPoint(unauthorizedHandler)
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.oauth2Login()
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
				.antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
				.antMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/gradeTicketCode/status/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/gradeTicketCode/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/users/checkUsernameAvailability", "/api/users/checkEmailAvailability").permitAll()
				.anyRequest().authenticated();

		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(customUserDetailsService)
				.passwordEncoder(passwordEncoder());
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

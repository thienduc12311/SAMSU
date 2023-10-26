package com.ftalk.samsu.repository;

import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.security.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(@NotBlank String username);

	Optional<User> findByEmail(@NotBlank String email);

	Boolean existsByUsername(@NotBlank String username);

	Boolean existsByRollnumber(@NotBlank String rollnumber);

	Boolean existsByEmail(@NotBlank String email);

	Optional<User> findByUsernameOrEmail(String username, String email);

	Optional<User> findById(Integer id);

	Set<User> findAllByIdIn(Set<Integer> userID);

	Optional<User> findByRollnumber(String rollnumber);

	default User getUser(UserPrincipal currentUser) {
		return getUserByName(currentUser.getUsername());
	}

	default User getUserByName(String username) {
		return findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	}
	default User getUserByRollnumber(String rollnumber) {
		return findByRollnumber(rollnumber)
				.orElseThrow(() -> new ResourceNotFoundException("User", "rollnumber", rollnumber));
	}
}

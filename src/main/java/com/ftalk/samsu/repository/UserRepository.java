package com.ftalk.samsu.repository;

import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.security.UserPrincipal;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(@NotBlank String username);

	Optional<User> findByEmail(@NotBlank String email);

	Boolean existsByUsername(@NotBlank String username);

	Boolean existsByRollnumber(@NotBlank String rollnumber);

	Boolean existsByEmail(@NotBlank String email);

	Optional<User> findByUsernameOrEmail(String username, String email);

	@NotNull Optional<User> findById(@NotNull Integer id);

	Set<User> findAllByIdIn(Set<Integer> userID);

	@Modifying
	@Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId AND u.password = :oldPassword")
	int updatePasswordById(String oldPassword, String newPassword, Integer userId);

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

package com.divergentsl.multitenant.repository;

import java.util.Optional;

import com.divergentsl.multitenant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);

	Optional<User> findByName(String usernameOrEmail);

}

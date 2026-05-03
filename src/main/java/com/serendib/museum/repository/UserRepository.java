package com.serendib.museum.repository;

import com.serendib.museum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides database operations for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email address.
     *
     * @param email user email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email user email
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

}

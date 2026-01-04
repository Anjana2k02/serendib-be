package com.serendib.museum.repository;

import com.serendib.museum.entity.OnboardingResponse;
import com.serendib.museum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for OnboardingResponse entity
 */
@Repository
public interface OnboardingResponseRepository extends JpaRepository<OnboardingResponse, Long> {

    /**
     * Find onboarding response by user
     *
     * @param user the user entity
     * @return Optional containing the onboarding response if found
     */
    Optional<OnboardingResponse> findByUser(User user);

    /**
     * Find onboarding response by user ID
     *
     * @param userId the user ID
     * @return Optional containing the onboarding response if found
     */
    Optional<OnboardingResponse> findByUserId(Long userId);

    /**
     * Check if onboarding response exists for a user
     *
     * @param user the user entity
     * @return true if response exists, false otherwise
     */
    boolean existsByUser(User user);

    /**
     * Delete onboarding response by user
     *
     * @param user the user entity
     */
    void deleteByUser(User user);
}

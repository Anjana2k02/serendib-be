package com.serendib.museum.repository;

import com.serendib.museum.entity.UserScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserScore entity.
 * Provides database operations for user score management.
 */
@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {

    /**
     * Find all scores for a specific user
     */
    List<UserScore> findByUserId(Long userId);

    /**
     * Find a specific score for a user-category pair
     */
    Optional<UserScore> findByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Find all scores for a specific category
     */
    List<UserScore> findByCategoryId(Long categoryId);

    /**
     * Check if a score exists for a user-category pair
     */
    boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Delete all scores for a specific user
     */
    void deleteByUserId(Long userId);
}

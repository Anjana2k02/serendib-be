package com.serendib.museum.service;

import com.serendib.museum.dto.request.DwellTimeRequest;
import com.serendib.museum.dto.request.UserScoreRequest;
import com.serendib.museum.dto.response.UserScoreResponse;

import java.util.List;

/**
 * Service interface for managing user scores
 */
public interface UserScoreService {

    /**
     * Save or update a user score for a specific category
     *
     * @param userId     the authenticated user's ID
     * @param requestDTO the user score request data
     * @return the saved user score response
     */
    UserScoreResponse saveOrUpdateUserScore(Long userId, UserScoreRequest requestDTO);

    /**
     * Track dwell time when user is standing near an artifact.
     * Looks up the artifact's category, finds or creates the user_score record
     * for that user+category pair, and increments the dwell_time.
     *
     * @param userId     the authenticated user's ID
     * @param requestDTO the dwell time tracking request
     * @return the updated user score response
     */
    UserScoreResponse trackDwellTime(Long userId, DwellTimeRequest requestDTO);

    /**
     * Get all scores for a specific user
     *
     * @param userId the user's ID
     * @return list of user score responses
     */
    List<UserScoreResponse> getUserScores(Long userId);

    /**
     * Get a specific score for a user-category pair
     *
     * @param userId     the user's ID
     * @param categoryId the category's ID
     * @return the user score response
     */
    UserScoreResponse getUserScoreByCategory(Long userId, Long categoryId);

    /**
     * Delete a specific user score
     *
     * @param userId     the user's ID
     * @param categoryId the category's ID
     */
    void deleteUserScore(Long userId, Long categoryId);
}

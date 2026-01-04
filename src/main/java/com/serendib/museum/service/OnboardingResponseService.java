package com.serendib.museum.service;

import com.serendib.museum.dto.OnboardingRequestDTO;
import com.serendib.museum.dto.OnboardingResponseDTO;

/**
 * Service interface for managing onboarding survey responses
 */
public interface OnboardingResponseService {

    /**
     * Save or update onboarding response for authenticated user
     *
     * @param userId the authenticated user's ID
     * @param requestDTO the onboarding request data
     * @return the saved onboarding response
     */
    OnboardingResponseDTO saveOnboardingResponse(Long userId, OnboardingRequestDTO requestDTO);

    /**
     * Get onboarding response for authenticated user
     *
     * @param userId the authenticated user's ID
     * @return the onboarding response if exists
     */
    OnboardingResponseDTO getOnboardingResponse(Long userId);

    /**
     * Check if user has completed onboarding
     *
     * @param userId the user's ID
     * @return true if onboarding completed, false otherwise
     */
    boolean hasCompletedOnboarding(Long userId);

    /**
     * Delete onboarding response for authenticated user
     *
     * @param userId the authenticated user's ID
     */
    void deleteOnboardingResponse(Long userId);
}

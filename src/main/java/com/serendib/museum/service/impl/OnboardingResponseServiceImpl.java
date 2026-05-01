package com.serendib.museum.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serendib.museum.dto.OnboardingRequestDTO;
import com.serendib.museum.dto.OnboardingResponseDTO;
import com.serendib.museum.entity.Category;
import com.serendib.museum.entity.OnboardingResponse;
import com.serendib.museum.entity.User;
import com.serendib.museum.entity.UserType;
import com.serendib.museum.exception.ResourceNotFoundException;
import com.serendib.museum.repository.CategoryRepository;
import com.serendib.museum.repository.OnboardingResponseRepository;
import com.serendib.museum.repository.UserRepository;
import com.serendib.museum.repository.UserTypeRepository;
import com.serendib.museum.service.OnboardingResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of OnboardingResponseService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingResponseServiceImpl implements OnboardingResponseService {

    private final OnboardingResponseRepository onboardingResponseRepository;
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public OnboardingResponseDTO saveOnboardingResponse(Long userId, OnboardingRequestDTO requestDTO) {
        log.info("Saving onboarding response for user ID: {}", userId);

        // Validate that user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Validate foreign visitor must have country
        if ("Foreign Visitor".equalsIgnoreCase(requestDTO.getVisitorType())) {
            if (requestDTO.getCountry() == null || requestDTO.getCountry().trim().isEmpty()) {
                throw new IllegalArgumentException("Country is required for foreign visitors");
            }
        }

        // Check if response already exists for this user
        OnboardingResponse response = onboardingResponseRepository.findByUser(user)
                .orElse(OnboardingResponse.builder()
                        .user(user)
                        .build());

        // Resolve UserType
        UserType userType = userTypeRepository.findByName(requestDTO.getUserType())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user type: " + requestDTO.getUserType()));

        // Resolve Categories (Interests)
        Set<Category> categories = requestDTO.getInterests().stream()
                .map(interest -> categoryRepository.findByName(interest)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid interest category: " + interest)))
                .collect(Collectors.toSet());

        // Update response fields
        response.setVisitorType(requestDTO.getVisitorType());
        response.setCountry(requestDTO.getCountry());
        response.setUserType(userType);
        response.setInterests(categories);
        response.setTimePreference(requestDTO.getTimePreference());
        response.setLanguagePreference(requestDTO.getLanguagePreference());

        // Save to database
        OnboardingResponse savedResponse = onboardingResponseRepository.save(response);

        log.info("Successfully saved onboarding response for user ID: {}", userId);
        return mapToDTO(savedResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OnboardingResponseDTO getOnboardingResponse(Long userId) {
        log.info("Fetching onboarding response for user ID: {}", userId);

        OnboardingResponse response = onboardingResponseRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding response not found for user ID: " + userId));

        return mapToDTO(response);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCompletedOnboarding(Long userId) {
        return onboardingResponseRepository.findByUserId(userId).isPresent();
    }

    @Override
    @Transactional
    public void deleteOnboardingResponse(Long userId) {
        log.info("Deleting onboarding response for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        onboardingResponseRepository.deleteByUser(user);

        log.info("Successfully deleted onboarding response for user ID: {}", userId);
    }

    /**
     * Map entity to DTO
     */
    private OnboardingResponseDTO mapToDTO(OnboardingResponse response) {
        List<String> interestsList = response.getInterests() != null ?
                response.getInterests().stream()
                        .map(Category::getName)
                        .collect(Collectors.toList()) : Collections.emptyList();

        return OnboardingResponseDTO.builder()
                .id(response.getId())
                .userId(response.getUser().getId())
                .visitorType(response.getVisitorType())
                .country(response.getCountry())
                .userType(response.getUserType().getName())
                .interests(interestsList)
                .timePreference(response.getTimePreference())
                .languagePreference(response.getLanguagePreference())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}

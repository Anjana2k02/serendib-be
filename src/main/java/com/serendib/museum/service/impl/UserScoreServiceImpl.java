package com.serendib.museum.service.impl;

import com.serendib.museum.dto.request.DwellTimeRequest;
import com.serendib.museum.dto.request.UserScoreRequest;
import com.serendib.museum.dto.response.UserScoreResponse;
import com.serendib.museum.entity.Artifact;
import com.serendib.museum.entity.Category;
import com.serendib.museum.entity.User;
import com.serendib.museum.entity.UserScore;
import com.serendib.museum.exception.ResourceNotFoundException;
import com.serendib.museum.repository.ArtifactRepository;
import com.serendib.museum.repository.CategoryRepository;
import com.serendib.museum.repository.UserRepository;
import com.serendib.museum.repository.UserScoreRepository;
import com.serendib.museum.service.UserScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserScoreService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserScoreServiceImpl implements UserScoreService {

    private final UserScoreRepository userScoreRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ArtifactRepository artifactRepository;

    @Override
    @Transactional
    public UserScoreResponse saveOrUpdateUserScore(Long userId, UserScoreRequest requestDTO) {
        log.info("Saving/updating user score for user ID: {}, category ID: {}", userId, requestDTO.getCategoryId());

        // Validate that user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Validate that category exists
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", requestDTO.getCategoryId()));

        // Check if score already exists for this user-category pair
        UserScore userScore = userScoreRepository.findByUserIdAndCategoryId(userId, requestDTO.getCategoryId())
                .orElse(UserScore.builder()
                        .user(user)
                        .category(category)
                        .build());

        // Update score fields
        userScore.setCurrent(requestDTO.getCurrent());
        userScore.setDwellTime(requestDTO.getDwellTime());

        // Save to database
        UserScore savedScore = userScoreRepository.save(userScore);

        log.info("Successfully saved user score for user ID: {}, category ID: {}", userId, requestDTO.getCategoryId());
        return mapToResponse(savedScore);
    }

    @Override
    @Transactional
    public UserScoreResponse trackDwellTime(Long userId, DwellTimeRequest requestDTO) {
        log.info("Tracking dwell time for user ID: {}, artifact ID: {}, activity: {}, duration: {}ms",
                userId, requestDTO.getArtifactId(), requestDTO.getActivity(), requestDTO.getDurationMs());

        // Only process 'standing' activity
        if (!"standing".equalsIgnoreCase(requestDTO.getActivity())) {
            throw new IllegalArgumentException(
                    "Dwell time is only tracked for 'standing' activity. Received: " + requestDTO.getActivity());
        }

        // Validate that user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Find the nearest artifact by ID
        Artifact artifact = artifactRepository.findById(requestDTO.getArtifactId())
                .orElseThrow(() -> new ResourceNotFoundException("Artifact", "id", requestDTO.getArtifactId()));

        if (artifact.getDeleted()) {
            throw new ResourceNotFoundException("Artifact", "id", requestDTO.getArtifactId());
        }

        // Get the nearest artifact's category (stored as a String in Artifact entity)
        String artifactCategoryName = artifact.getCategory();

        // Resolve the Category entity by name to get the category ID
        Category category = categoryRepository.findByName(artifactCategoryName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found for artifact category name: " + artifactCategoryName));

        // Find or create the UserScore record for this user + nearest category ID
        UserScore userScore = userScoreRepository.findByUserIdAndCategoryId(userId, category.getId())
                .orElse(UserScore.builder()
                        .user(user)
                        .category(category)
                        .current(0.0)
                        .dwellTime(0L)
                        .build());

        // INCREMENT dwell_time in milliseconds (always accumulating, never replacing)
        Long currentDwellTime = userScore.getDwellTime() != null ? userScore.getDwellTime() : 0L;
        userScore.setDwellTime(currentDwellTime + requestDTO.getDurationMs());

        // Recalculate current score based on accumulated dwell time (in milliseconds)
        // Score formula: 1 point per 10,000ms (10 seconds) of dwell time
        userScore.setCurrent(userScore.getDwellTime() / 10000.0);

        // Save to database
        UserScore savedScore = userScoreRepository.save(userScore);

        log.info("Successfully updated dwell time for user ID: {}, category: {} (ID: {}). " +
                        "New dwell_time: {}ms, new score: {}",
                userId, category.getName(), category.getId(),
                savedScore.getDwellTime(), savedScore.getCurrent());

        return mapToResponse(savedScore);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserScoreResponse> getUserScores(Long userId) {
        log.info("Fetching all scores for user ID: {}", userId);

        List<UserScore> scores = userScoreRepository.findByUserId(userId);
        return scores.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserScoreResponse getUserScoreByCategory(Long userId, Long categoryId) {
        log.info("Fetching score for user ID: {}, category ID: {}", userId, categoryId);

        UserScore userScore = userScoreRepository.findByUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User score not found for user ID: " + userId + " and category ID: " + categoryId));

        return mapToResponse(userScore);
    }

    @Override
    @Transactional
    public void deleteUserScore(Long userId, Long categoryId) {
        log.info("Deleting user score for user ID: {}, category ID: {}", userId, categoryId);

        UserScore userScore = userScoreRepository.findByUserIdAndCategoryId(userId, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User score not found for user ID: " + userId + " and category ID: " + categoryId));

        userScoreRepository.delete(userScore);

        log.info("Successfully deleted user score for user ID: {}, category ID: {}", userId, categoryId);
    }

    /**
     * Map entity to response DTO
     */
    private UserScoreResponse mapToResponse(UserScore userScore) {
        return UserScoreResponse.builder()
                .id(userScore.getId())
                .userId(userScore.getUser().getId())
                .categoryId(userScore.getCategory().getId())
                .categoryName(userScore.getCategory().getName())
                .current(userScore.getCurrent())
                .dwellTime(userScore.getDwellTime())
                .createdAt(userScore.getCreatedAt())
                .updatedAt(userScore.getUpdatedAt())
                .build();
    }
}


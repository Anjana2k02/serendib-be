package com.serendib.museum.controller;

import com.serendib.museum.dto.request.DwellTimeRequest;
import com.serendib.museum.dto.request.UserScoreRequest;
import com.serendib.museum.dto.response.UserScoreResponse;
import com.serendib.museum.entity.User;
import com.serendib.museum.service.UserScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing user scores per category
 */
@RestController
@RequestMapping("/api/v1/user-scores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Scores", description = "User score management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserScoreController {

    private final UserScoreService userScoreService;

    /**
     * Track dwell time when user is standing near an artifact.
     * The mobile app should call this endpoint periodically while the user
     * remains standing near an artifact. The dwell_time always accumulates in milliseconds.
     */
    @PostMapping("/track-dwell-time")
    @Operation(
            summary = "Track dwell time near artifact",
            description = "Track user's standing time near an artifact in milliseconds. " +
                    "Resolves the nearest artifact's category ID and increments dwell_time in user_scores. " +
                    "Only processes 'standing' activity. dwell_time always accumulates (never resets)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dwell time tracked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or activity is not 'standing'"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Artifact or category not found")
    })
    public ResponseEntity<Map<String, Object>> trackDwellTime(
            @Valid @RequestBody DwellTimeRequest requestDTO) {

        log.info("Received dwell time tracking request for category ID: {}", requestDTO.getCategoryId());

        Long userId = getAuthenticatedUserId();
        UserScoreResponse responseDTO = userScoreService.trackDwellTime(userId, requestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dwell time tracked successfully");
        response.put("data", responseDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * Save or update a user score for a category
     */
    @PostMapping
    @Operation(
            summary = "Save or update user score",
            description = "Save or update a user's score and dwell time for a specific category"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User score saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User or category not found")
    })
    public ResponseEntity<Map<String, Object>> saveOrUpdateUserScore(
            @Valid @RequestBody UserScoreRequest requestDTO) {

        log.info("Received user score save/update request");

        Long userId = getAuthenticatedUserId();
        UserScoreResponse responseDTO = userScoreService.saveOrUpdateUserScore(userId, requestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User score saved successfully");
        response.put("data", responseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all scores for the authenticated user
     */
    @GetMapping
    @Operation(
            summary = "Get all user scores",
            description = "Retrieve all scores for the authenticated user across all categories"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User scores retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    public ResponseEntity<Map<String, Object>> getUserScores() {

        log.info("Fetching all scores for authenticated user");

        Long userId = getAuthenticatedUserId();
        List<UserScoreResponse> scores = userScoreService.getUserScores(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", scores);

        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific score for a user-category pair
     */
    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "Get user score by category",
            description = "Retrieve a specific score for the authenticated user in a given category"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User score retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Score not found for this category")
    })
    public ResponseEntity<Map<String, Object>> getUserScoreByCategory(
            @PathVariable Long categoryId) {

        log.info("Fetching score for authenticated user, category ID: {}", categoryId);

        Long userId = getAuthenticatedUserId();
        UserScoreResponse scoreResponse = userScoreService.getUserScoreByCategory(userId, categoryId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", scoreResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a specific user score
     */
    @DeleteMapping("/category/{categoryId}")
    @Operation(
            summary = "Delete user score",
            description = "Delete a specific score for the authenticated user in a given category"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User score deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Score not found for this category")
    })
    public ResponseEntity<Map<String, Object>> deleteUserScore(
            @PathVariable Long categoryId) {

        log.info("Deleting score for authenticated user, category ID: {}", categoryId);

        Long userId = getAuthenticatedUserId();
        userScoreService.deleteUserScore(userId, categoryId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User score deleted successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Extract authenticated user ID from security context
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }

        throw new IllegalStateException("Unable to extract user ID from authentication");
    }
}


package com.serendib.museum.controller;

import com.serendib.museum.dto.OnboardingRequestDTO;
import com.serendib.museum.dto.OnboardingResponseDTO;
import com.serendib.museum.entity.User;
import com.serendib.museum.service.OnboardingResponseService;
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
import java.util.Map;

/**
 * REST controller for managing onboarding survey responses
 */
@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Onboarding", description = "Onboarding survey management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class OnboardingController {

    private final OnboardingResponseService onboardingResponseService;

    /**
     * Save or update onboarding response for authenticated user
     */
    @PostMapping
    @Operation(
            summary = "Submit onboarding survey",
            description = "Save or update onboarding survey responses for the authenticated user. This should be called after successful user registration."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Onboarding response saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, Object>> saveOnboardingResponse(
            @Valid @RequestBody OnboardingRequestDTO requestDTO) {

        log.info("Received onboarding response submission");

        // Get authenticated user ID from security context
        Long userId = getAuthenticatedUserId();

        // Save onboarding response
        OnboardingResponseDTO responseDTO = onboardingResponseService.saveOnboardingResponse(userId, requestDTO);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Onboarding response saved successfully");
        response.put("data", responseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get onboarding response for authenticated user
     */
    @GetMapping
    @Operation(
            summary = "Get onboarding survey response",
            description = "Retrieve onboarding survey response for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding response retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Onboarding response not found")
    })
    public ResponseEntity<Map<String, Object>> getOnboardingResponse() {

        log.info("Fetching onboarding response for authenticated user");

        // Get authenticated user ID
        Long userId = getAuthenticatedUserId();

        // Fetch onboarding response
        OnboardingResponseDTO responseDTO = onboardingResponseService.getOnboardingResponse(userId);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", responseDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * Check if authenticated user has completed onboarding
     */
    @GetMapping("/status")
    @Operation(
            summary = "Check onboarding completion status",
            description = "Check if the authenticated user has completed the onboarding survey"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    public ResponseEntity<Map<String, Object>> getOnboardingStatus() {

        log.info("Checking onboarding status for authenticated user");

        // Get authenticated user ID
        Long userId = getAuthenticatedUserId();

        // Check completion status
        boolean completed = onboardingResponseService.hasCompletedOnboarding(userId);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("completed", completed);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete onboarding response for authenticated user
     */
    @DeleteMapping
    @Operation(
            summary = "Delete onboarding survey response",
            description = "Delete onboarding survey response for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding response deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, Object>> deleteOnboardingResponse() {

        log.info("Deleting onboarding response for authenticated user");

        // Get authenticated user ID
        Long userId = getAuthenticatedUserId();

        // Delete onboarding response
        onboardingResponseService.deleteOnboardingResponse(userId);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Onboarding response deleted successfully");

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

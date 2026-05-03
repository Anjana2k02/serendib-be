package com.serendib.museum.controller;

import com.serendib.museum.dto.request.LoginRequest;
import com.serendib.museum.dto.request.RegisterRequest;
import com.serendib.museum.dto.response.AuthenticationResponse;
import com.serendib.museum.exception.ErrorResponse;
import com.serendib.museum.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Handles user registration and login operations.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user in the system.
     *
     * @param request registration request containing user details
     * @return authentication response with JWT tokens
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns JWT access and refresh tokens. " +
                         "The password will be securely hashed before storage."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with this email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse response = authenticationService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * @param request login request containing credentials
     * @return authentication response with JWT tokens
     */
    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user with email and password, returns JWT access and refresh tokens. " +
                         "Use the access token in the 'Authorization' header as 'Bearer {token}' for subsequent requests."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param authHeader Authorization header containing "Bearer {refreshToken}"
     * @return new authentication response with a fresh access token
     */
    @PostMapping("/refresh-token")
    @Operation(
            summary = "Refresh access token",
            description = "Exchange a valid refresh token for a new access token. " +
                         "Send the refresh token in the Authorization header as 'Bearer {refreshToken}'."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "New access token issued successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token is invalid or expired - user must log in again",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Missing or malformed Authorization header", "/api/v1/auth/refresh-token"));
        }

        String refreshToken = authHeader.substring(7);
        AuthenticationResponse response = authenticationService.refreshToken(refreshToken);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Refresh token is invalid or expired. Please log in again.", "/api/v1/auth/refresh-token"));
        }

        return ResponseEntity.ok(response);
    }
}

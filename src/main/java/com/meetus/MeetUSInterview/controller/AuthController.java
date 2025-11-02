package com.meetus.MeetUSInterview.controller;

import com.meetus.MeetUSInterview.dto.request.auth.LoginRequest;
import com.meetus.MeetUSInterview.dto.request.auth.RegisterRequest;
import com.meetus.MeetUSInterview.dto.response.APIResponse;
import com.meetus.MeetUSInterview.dto.response.auth.AuthResponse;
import com.meetus.MeetUSInterview.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

/**
 * REST Controller for Authentication operations
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {

    private final UserService userService;

    /**
     * Register a new user
     * @param request register request
     * @return authentication response with token
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with hashed password and returns JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request data or email already exists",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        )
    })
    public ResponseEntity<APIResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());

        AuthResponse response = userService.register(request);

        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(response, "User registered successfully"));
    }

    /**
     * Login user
     * @param request login request
     * @return authentication response with token
     */
    @PostMapping("/login")
    @Operation(
        summary = "Login user",
        description = "Authenticates user and returns JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User logged in successfully",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid email or password",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        )
    })
    public ResponseEntity<APIResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());

        AuthResponse response = userService.login(request);

        log.info("User logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(APIResponse.success(response, "User logged in successfully"));
    }

    /**
     * Logout user (client-side token invalidation)
     * @return success response
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Logout user",
        description = "Invalidates the current user session (client should discard the token)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User logged out successfully",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = APIResponse.class))
        )
    })
    public ResponseEntity<APIResponse<Void>> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            String email = authentication.getName();
            log.info("User logged out: {}", email);
        }

        // Clear security context
        SecurityContextHolder.clearContext();

        // Note: In a stateless JWT system, logout is primarily handled client-side
        // by discarding the token. For more robust logout, consider implementing
        // a token blacklist or shorter expiration times.
        
        return ResponseEntity.ok(APIResponse.success(null, "User logged out successfully"));
    }
}

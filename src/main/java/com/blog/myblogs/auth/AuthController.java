package com.blog.myblogs.auth;

import com.blog.myblogs.common.ApiResponse;
import com.blog.myblogs.common.ResponseGenerator;
import com.blog.myblogs.security.JwtTokenProvider;
import com.blog.myblogs.user.UserService;
import com.blog.myblogs.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
            @Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserResponseDTO user = userService.registerUser(registrationDTO);
        return ResponseGenerator.generateResponse(
                "User registered successfully. Please check your email for verification.",
                HttpStatus.CREATED, user);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponseDTO>> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        // Update last login time
        userService.updateLastLogin(authentication.getName());

        // Get user details by email
        UserResponseDTO userResponse = userService.getUserByEmail(loginDTO.getEmail());

        JwtAuthResponseDTO authResponse = JwtAuthResponseDTO.builder()
                .accessToken(jwt)
                .user(userResponse)
                .build();

        return ResponseGenerator.generateResponse("User signed in successfully", HttpStatus.OK, authResponse);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseGenerator.generateResponse("Email verified successfully", HttpStatus.OK,
                "Your email has been verified. You can now login to your account.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        userService.requestPasswordReset(requestDTO);
        return ResponseGenerator.generateResponse("Password reset email sent", HttpStatus.OK,
                "If an account with that email exists, we've sent a password reset link.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody PasswordResetDTO resetDTO) {
        userService.resetPassword(resetDTO);
        return ResponseGenerator.generateResponse("Password reset successfully", HttpStatus.OK,
                "Your password has been reset successfully. You can now login with your new password.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<JwtAuthResponseDTO>> refreshToken(
            @RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.substring(7); // Remove "Bearer " prefix

        if (tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);
            String newToken = tokenProvider.generateTokenFromUsername(username);

            UserResponseDTO userResponse = userService.getUserByEmail(username);

            JwtAuthResponseDTO authResponse = JwtAuthResponseDTO.builder()
                    .accessToken(newToken)
                    .user(userResponse)
                    .build();

            return ResponseGenerator.generateResponse("Token refreshed successfully", HttpStatus.OK, authResponse);
        } else {
            return ResponseGenerator.generateResponse("Invalid token", HttpStatus.UNAUTHORIZED, null);
        }
    }
}

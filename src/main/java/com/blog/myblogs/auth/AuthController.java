package com.blog.myblogs.auth;

import com.blog.myblogs.common.ApiResponse;
import com.blog.myblogs.common.ResponseGenerator;
import com.blog.myblogs.exceptions.ResourceNotFoundException;
import com.blog.myblogs.security.JwtTokenProvider;
import com.blog.myblogs.user.UserService;
import com.blog.myblogs.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                "User registered successfully.",
                HttpStatus.CREATED, user);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponseDTO>> authenticateUser(@Valid @RequestBody LoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        // Update last login time
        userService.updateLastLogin(authentication.getName());

        // Get user details by username
        UserResponseDTO userResponse = userService.getUserByUsername(loginDTO.getUsername());

        JwtAuthResponseDTO authResponse = JwtAuthResponseDTO.builder()
                .accessToken(jwt)
                .user(userResponse)
                .build();

        return ResponseGenerator.generateResponse("User signed in successfully", HttpStatus.OK, authResponse);

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

    @PostMapping("/forgot-username")
    public ResponseEntity<ApiResponse<String>> forgotUsername(@Valid @RequestBody ForgotUsernameRequestDTO requestDTO) {
        userService.sendForgotUsernameEmail(requestDTO.getEmail());
        return ResponseGenerator.generateResponse("If the email exists, your username has been sent.", HttpStatus.OK,
                null);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<JwtAuthResponseDTO>> refreshToken(
            @RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.substring(7); // Remove "Bearer " prefix

        if (tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);
            String newToken = tokenProvider.generateTokenFromUsername(username);

            UserResponseDTO userResponse = userService.getUserByUsername(username);

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

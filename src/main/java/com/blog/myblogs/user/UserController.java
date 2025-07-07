package com.blog.myblogs.user;

import com.blog.myblogs.common.ApiResponse;
import com.blog.myblogs.common.ResponseGenerator;
import com.blog.myblogs.user.dto.UserProfileDTO;
import com.blog.myblogs.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserResponseDTO user = userService.getUserByEmail(username);
        return ResponseGenerator.generateResponse("User profile fetched successfully", HttpStatus.OK, user);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserProfile(@PathVariable String username) {
        UserResponseDTO user = userService.getUserByEmail(username);
        return ResponseGenerator.generateResponse("User profile fetched successfully", HttpStatus.OK, user);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(@Valid @RequestBody UserProfileDTO profileDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserResponseDTO updatedUser = userService.updateUserProfile(currentUser.getId(), profileDTO);
        return ResponseGenerator.generateResponse("Profile updated successfully", HttpStatus.OK, updatedUser);
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<UserResponseDTO> users = userService.getAllUsers(page, size);
        return ResponseGenerator.generateResponse("Users fetched successfully", HttpStatus.OK, users);
    }

    @GetMapping("/admin/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<UserResponseDTO> users = userService.getUsersByRole(role, page, size);
        return ResponseGenerator.generateResponse("Users fetched successfully", HttpStatus.OK, users);
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<UserResponseDTO> users = userService.searchUsers(keyword, page, size);
        return ResponseGenerator.generateResponse("Search results fetched successfully", HttpStatus.OK, users);
    }

    @GetMapping("/admin/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getUserCount() {
        long count = userService.getUserCount();
        return ResponseGenerator.generateResponse("User count fetched successfully", HttpStatus.OK, count);
    }

    @GetMapping("/admin/count/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getUserCountByRole(@PathVariable Role role) {
        long count = userService.getUserCountByRole(role);
        return ResponseGenerator.generateResponse("User count by role fetched successfully", HttpStatus.OK, count);
    }

    @PatchMapping("/admin/{userId}/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserRole(
            @PathVariable Long userId,
            @PathVariable Role role) {
        UserResponseDTO updatedUser = userService.updateUserRole(userId, role);
        return ResponseGenerator.generateResponse("User role updated successfully", HttpStatus.OK, updatedUser);
    }

    @PatchMapping("/admin/{userId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> enableUser(@PathVariable Long userId) {
        userService.enableUser(userId);
        return ResponseGenerator.generateResponse("User enabled successfully", HttpStatus.OK, null);
    }

    @PatchMapping("/admin/{userId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
        return ResponseGenerator.generateResponse("User disabled successfully", HttpStatus.OK, null);
    }

    @GetMapping("/admin/{userId}/delete-preview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUserPreview(@PathVariable Long userId) {
        int postCount = 0;
        int commentCount = 0;
        try {
            var user = userService.getUserEntityById(userId);
            postCount = user.getPosts() != null ? user.getPosts().size() : 0;
            commentCount = user.getComments() != null ? user.getComments().size() : 0;
        } catch (Exception ignored) {
        }
        String message = "Deleting this user will also delete " + postCount + " posts and " + commentCount
                + " comments associated with this user. Are you sure you want to proceed?";
        return ResponseGenerator.generateResponse(message, HttpStatus.OK, null);
    }

    @DeleteMapping("/admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId,
            @RequestParam(value = "confirm", required = false) Boolean confirm) {
        int postCount = 0;
        int commentCount = 0;
        try {
            var user = userService.getUserEntityById(userId);
            postCount = user.getPosts() != null ? user.getPosts().size() : 0;
            commentCount = user.getComments() != null ? user.getComments().size() : 0;
        } catch (Exception ignored) {
        }
        if ((postCount > 0 || commentCount > 0) && (confirm == null || !confirm)) {
            String message = "Confirmation required: Deleting this user will also delete " + postCount + " posts and "
                    + commentCount + " comments.";
            return ResponseGenerator.generateResponse(message, HttpStatus.BAD_REQUEST, null);
        }
        userService.deleteUser(userId);
        String message = "User deleted successfully.";
        return ResponseGenerator.generateResponse(message, HttpStatus.OK, null);
    }
}

package com.blog.myblogs.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotUsernameRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;
}

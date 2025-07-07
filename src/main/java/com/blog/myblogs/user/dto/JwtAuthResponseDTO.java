package com.blog.myblogs.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthResponseDTO {

    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private UserResponseDTO user;
}

package com.blog.myblogs.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
    private String websiteUrl;
    private String twitterHandle;
    private String linkedinUrl;
    private String githubUrl;
}

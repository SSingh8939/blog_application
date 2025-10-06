package com.blog.myblogs.post;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private String title;

    private String content;

    private Long adminId;
    private Long categoryId;
    private MultipartFile image;
}

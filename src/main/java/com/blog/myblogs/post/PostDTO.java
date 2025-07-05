package com.blog.myblogs.post;

import jakarta.persistence.Column;
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

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private Long adminId;
    private Long categoryId;
}

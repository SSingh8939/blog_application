package com.blog.myblogs.post;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.blog.myblogs.comment.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Post {

    @Id
    @GeneratedValue
    private long id;

    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    private long adminId;
    private Long categoryId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long likes = 0L;
    private Long disLikes = 0L;
    private boolean published = false;

    @OneToMany
    private List<Comment> comments;

}

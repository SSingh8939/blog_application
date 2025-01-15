package com.blog.myblogs.comment;

import java.time.LocalDateTime;
import java.util.List;

import com.blog.myblogs.post.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue
    private long id;

    private String content;
    private String author;
    private LocalDateTime createdAt;

    @ManyToOne
    private Post blog;

    @ManyToOne
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> replies;
}

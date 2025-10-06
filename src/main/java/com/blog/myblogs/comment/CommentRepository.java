package com.blog.myblogs.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPost_Id(Long postId);

    List<Comment> findByAuthor_Id(Long id);

}

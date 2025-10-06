package com.blog.myblogs.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.myblogs.exceptions.ResourceNotFoundException;
import com.blog.myblogs.post.Post;
import com.blog.myblogs.post.PostRepository;
import com.blog.myblogs.user.User;
import com.blog.myblogs.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

        @Autowired
        private CommentRepository commentRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private PostRepository postRepository;

        public List<Comment> getRootLevelCommentsWithReplies(List<Comment> allComments) {
                // Group replies by parent
                Map<Long, List<Comment>> repliesMap = allComments.stream()
                                .filter(c -> c.getParent() != null)
                                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

                // Assign replies to their parent comment
                allComments.forEach(comment -> {
                        List<Comment> replies = repliesMap.get(comment.getId());
                        comment.setReplies(replies != null ? replies : new ArrayList<>());
                });

                // Get only top-level comments
                List<Comment> rootLevelComments = allComments.stream()
                                .filter(c -> c.getParent() == null)
                                .collect(Collectors.toList());

                return rootLevelComments;
        }

        public List<Comment> getAllComments() {
                List<Comment> allComments = commentRepository.findAll();
                return allComments;
        }

        public List<Comment> getAllCommentsWithRepliesByPostId(Long id) {
                List<Comment> allComments = commentRepository.findByPost_Id(id);
                return getRootLevelCommentsWithReplies(allComments);
        }

        public List<Comment> getAllCommentsByAuthorId(Long id) {
                List<Comment> allComments = commentRepository.findByAuthor_Id(id);
                return allComments;
        }

        public Comment saveComment(CommentDTO dto) {
                User author = userRepository.findById(dto.getAuthorId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with id: " + dto.getAuthorId()));
                Post post = postRepository.findById(dto.getPostId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Post not found with id: " + dto.getPostId()));
                Comment parent = null;
                if (dto.getParentId() != null) {
                        parent = commentRepository.findById(dto.getParentId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Parent comment not found with id: " + dto.getParentId()));
                }
                Comment comment = Comment.builder()
                                .content(dto.getContent())
                                .author(author)
                                .post(post)
                                .parent(parent)
                                .build();

                Comment saved = commentRepository.save(comment);
                return saved;
        }

        public Comment updateComment(Long id, CommentDTO dto) {
                Comment existing = commentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));

                User author = userRepository.findById(dto.getAuthorId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with id: " + dto.getAuthorId()));

                existing.setContent(dto.getContent());
                existing.setAuthor(author);
                existing.setUpdatedAt(LocalDateTime.now());

                Comment saved = commentRepository.save(existing);
                return saved;
        }

        public void deleteComment(Long id) {
                Comment comment = commentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));

                commentRepository.delete(comment);
        }
}

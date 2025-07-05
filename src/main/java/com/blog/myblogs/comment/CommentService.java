package com.blog.myblogs.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.myblogs.exceptions.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

        @Autowired
        private CommentRepository commentRepository;

        public List<Comment> getAllCommentsWithReplies() {
                List<Comment> allComments = commentRepository.findAll();

                // Group replies by parentId
                Map<Long, List<Comment>> repliesMap = allComments.stream()
                                .filter(c -> c.getParentId() != null)
                                .collect(Collectors.groupingBy(Comment::getParentId));

                // Assign replies to their parent comment
                allComments.forEach(comment -> {
                        List<Comment> replies = repliesMap.get(comment.getId());
                        comment.setReplies(replies != null ? replies : new ArrayList<>());
                });

                // Get only top-level comments
                List<Comment> topLevelComments = allComments.stream()
                                .filter(c -> c.getParentId() == null)
                                .collect(Collectors.toList());

                return topLevelComments;
        }

        public Comment saveComment(CommentDTO dto) {
                Comment comment = Comment.builder()
                                .content(dto.getContent())
                                .author(dto.getAuthor())
                                .parentId(dto.getParentId())
                                .build();

                Comment saved = commentRepository.save(comment);
                return saved;
        }

        public Comment updateComment(Long id, CommentDTO dto) {
                Comment existing = commentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));

                Comment updated = existing.toBuilder()
                                .content(dto.getContent())
                                .author(dto.getAuthor())
                                .updatedAt(LocalDateTime.now())
                                .build();

                Comment saved = commentRepository.save(updated);
                return saved;
        }

        public Comment addReply(CommentDTO dto) {
                commentRepository.findById(dto.getParentId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("Parent comment not found with ID: "
                                                                + dto.getParentId()));

                Comment reply = Comment.builder()
                                .content(dto.getContent())
                                .author(dto.getAuthor())
                                .parentId(dto.getParentId())
                                .build();

                Comment saved = commentRepository.save(reply);
                return saved;
        }

        public void deleteComment(Long id) {
                Comment comment = commentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));

                commentRepository.delete(comment);
        }
}

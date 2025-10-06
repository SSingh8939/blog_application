package com.blog.myblogs.comment;

import com.blog.myblogs.common.ApiResponse;
import com.blog.myblogs.common.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Comment>>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ResponseGenerator.generateResponse("Comments fetched successfully", HttpStatus.OK, comments);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<Comment>>> getAllCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getAllCommentsWithRepliesByPostId(postId);
        return ResponseGenerator.generateResponse("Comments fetched successfully", HttpStatus.OK, comments);
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<ApiResponse<List<Comment>>> getAllCommentsByAuthorId(@PathVariable Long authorId) {
        List<Comment> comments = commentService.getAllCommentsByAuthorId(authorId);
        return ResponseGenerator.generateResponse("Comments fetched successfully", HttpStatus.OK, comments);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> saveComment(@RequestBody CommentDTO comment) {
        Comment saved = commentService.saveComment(comment);
        return ResponseGenerator.generateResponse("Comment saved successfully", HttpStatus.CREATED, saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Comment>> updateComment(@PathVariable Long id, @RequestBody CommentDTO comment) {
        Comment updated = commentService.updateComment(id, comment);
        return ResponseGenerator.generateResponse("Comment updated successfully", HttpStatus.OK, updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseGenerator.generateResponse("Comment deleted successfully", HttpStatus.OK, null);
    }
}

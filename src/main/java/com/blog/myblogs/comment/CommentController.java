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
        List<Comment> comments = commentService.getAllCommentsWithReplies();
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

    @PostMapping("/reply")
    public ResponseEntity<ApiResponse<Comment>> addReply(@RequestBody CommentDTO reply) {
        Comment savedReply = commentService.addReply(reply);
        return ResponseGenerator.generateResponse("Reply added successfully", HttpStatus.CREATED, savedReply);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseGenerator.generateResponse("Comment deleted successfully", HttpStatus.OK, null);
    }
}

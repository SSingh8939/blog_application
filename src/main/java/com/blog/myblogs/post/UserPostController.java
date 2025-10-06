package com.blog.myblogs.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.myblogs.common.ApiResponse;
import com.blog.myblogs.common.ResponseGenerator;

@RestController
@RequestMapping("/api/user/post")
@CrossOrigin("*")
public class UserPostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getAllPublishedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.getAllPublishedPosts(page - 1, size);
        return ResponseGenerator.generateResponse("Posts fetched successfully", HttpStatus.OK, posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> PostById(@PathVariable Long id) {
        Post post = postService.getSinglePublishedPostWithComment(id);
        return ResponseGenerator.generateResponse("Post fetched successfully", HttpStatus.OK, post);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Post>>> getPostsByCategory(
            @PathVariable long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.getPublishedPostsByCategory(categoryId, page - 1, size);
        return ResponseGenerator.generateResponse("Posts by category fetched successfully", HttpStatus.OK, posts);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Post>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.searchPublishedPosts(keyword, page - 1, size);
        return ResponseGenerator.generateResponse("Search results fetched successfully", HttpStatus.OK, posts);
    }

    @PatchMapping("/like/{postId}")
    public ResponseEntity<ApiResponse<Post>> likePost(@PathVariable long postId) {
        Post liked = postService.likePost(postId);
        return ResponseGenerator.generateResponse("Post liked", HttpStatus.OK, liked);
    }

    @PatchMapping("/undo-like/{postId}")
    public ResponseEntity<ApiResponse<Post>> undoLike(@PathVariable long postId) {
        Post result = postService.undoLike(postId);
        return ResponseGenerator.generateResponse("Post like undone", HttpStatus.OK, result);
    }

    @PatchMapping("/dislike/{postId}")
    public ResponseEntity<ApiResponse<Post>> disLikePost(@PathVariable long postId) {
        Post disliked = postService.disLikePost(postId);
        return ResponseGenerator.generateResponse("Post disliked", HttpStatus.OK, disliked);
    }

    @PatchMapping("/undo-dislike/{postId}")
    public ResponseEntity<ApiResponse<Post>> undoDislike(@PathVariable long postId) {
        Post result = postService.undoDislike(postId);
        return ResponseGenerator.generateResponse("Post dislike undone", HttpStatus.OK, result);
    }

}
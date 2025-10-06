package com.blog.myblogs.post;

import com.blog.myblogs.common.ApiResponse;
import com.blog.myblogs.common.ResponseGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/post")
public class AdminPostController {

    @Autowired
    private PostService postService;

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getPostsCount() {
        long count = postService.getAllPostCount();
        return ResponseGenerator.generateResponse("Post count fetched successfully", HttpStatus.OK, count);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.getAllPosts(page - 1, size);
        return ResponseGenerator.generateResponse("Posts fetched successfully", HttpStatus.OK, posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> PostById(@PathVariable Long id) {
        Post post = postService.getSinglePostWithComment(id);
        return ResponseGenerator.generateResponse("Post fetched successfully", HttpStatus.OK, post);
    }

    @GetMapping("/count/published")
    public ResponseEntity<ApiResponse<Long>> getPublishedPostsCount() {
        long count = postService.getAllPublishedPostCount();
        return ResponseGenerator.generateResponse("Published post count fetched successfully", HttpStatus.OK, count);
    }

    @GetMapping("/published")
    public ResponseEntity<ApiResponse<List<Post>>> getAllPublishedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.getAllPublishedPosts(page - 1, size);
        return ResponseGenerator.generateResponse("Published posts fetched successfully", HttpStatus.OK, posts);
    }

    @GetMapping("/count/unpublished")
    public ResponseEntity<ApiResponse<Long>> getUnpublishedPostsCount() {
        long count = postService.getAllUnpublishedPostCount();
        return ResponseGenerator.generateResponse("Unpublished post count fetched successfully", HttpStatus.OK, count);
    }

    @GetMapping("/unpublished")
    public ResponseEntity<ApiResponse<List<Post>>> getAllUnpublishedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.getAllUnpublishedPosts(page - 1, size);
        return ResponseGenerator.generateResponse("Unpublished posts fetched successfully", HttpStatus.OK, posts);
    }

    @GetMapping("/count/category/{id}")
    public ResponseEntity<ApiResponse<Long>> getPostsCountByCategory(@PathVariable long id) {
        long count = postService.getAllPostCountByCategory(id);
        return ResponseGenerator.generateResponse("Post count by category fetched successfully", HttpStatus.OK, count);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Post>>> getPostsByCategory(
            @PathVariable long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.getPostsByCategory(categoryId, page - 1, size);
        return ResponseGenerator.generateResponse("Posts by category fetched successfully", HttpStatus.OK, posts);
    }

    @GetMapping("/count/admin/{id}")
    public ResponseEntity<ApiResponse<Long>> getPostsCountByAdmin(@PathVariable long id) {
        long count = postService.getAllPostCountByAdmin(id);
        return ResponseGenerator.generateResponse("Post count by admin fetched successfully", HttpStatus.OK, count);
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<ApiResponse<List<Post>>> getPostsByAdmin(
            @PathVariable long adminId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.getPostsByAdmin(adminId, page - 1, size);
        return ResponseGenerator.generateResponse("Posts by admin fetched successfully", HttpStatus.OK, posts);
    }

    @GetMapping("/count/search")
    public ResponseEntity<ApiResponse<Long>> getPostsCountBySearch(@RequestParam String keyword) {
        long count = postService.getAllPostCountBySearch(keyword);
        return ResponseGenerator.generateResponse("Search result count fetched successfully", HttpStatus.OK, count);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Post>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Post> posts = postService.searchPosts(keyword, page - 1, size);
        return ResponseGenerator.generateResponse("Search results fetched successfully", HttpStatus.OK, posts);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<Post>> savePost(@ModelAttribute PostDTO dto) {
        Post saved = postService.savePost(dto, dto.getImage());
        return ResponseGenerator.generateResponse("Post saved successfully", HttpStatus.CREATED, saved);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<Post>> saveAndPublishPost(@ModelAttribute PostDTO dto) {
        Post saved = postService.saveAndPublishPost(dto, dto.getImage());
        return ResponseGenerator.generateResponse("Post saved and published successfully", HttpStatus.CREATED, saved);
    }

    @PatchMapping("/publish/{id}")
    public ResponseEntity<ApiResponse<Post>> publishPost(@PathVariable long id) {
        Post published = postService.publish(id);
        return ResponseGenerator.generateResponse("Post published successfully", HttpStatus.OK, published);
    }

    @PatchMapping("/unpublish/{id}")
    public ResponseEntity<ApiResponse<Post>> unpublishPost(@PathVariable long id) {
        Post published = postService.unpublish(id);
        return ResponseGenerator.generateResponse("Post unpublished successfully", HttpStatus.OK, published);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<Post>> updatePost(@PathVariable Long id, @ModelAttribute PostDTO post) {
        Post updated = postService.updatePost(id, post, post.getImage());
        return ResponseGenerator.generateResponse("Post updated successfully", HttpStatus.OK, updated);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
        return ResponseGenerator.generateResponse("Post deleted successfully", HttpStatus.OK, null);
    }

}

package com.blog.myblogs.post;

import com.blog.myblogs.common.ApiResponse;
import com.blog.myblogs.common.ResponseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/post")
public class PostController {

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
    public ResponseEntity<ApiResponse<Post>> savePost(
            @RequestPart("post") PostDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        Post saved = postService.savePost(dto, image);
        return ResponseGenerator.generateResponse("Post saved successfully", HttpStatus.CREATED, saved);
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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> updatePost(@PathVariable Long id, @RequestPart PostDTO post,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Post updated = postService.updatePost(id, post, image);
        return ResponseGenerator.generateResponse("Post updated successfully", HttpStatus.OK, updated);
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

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
        return ResponseGenerator.generateResponse("Post deleted successfully", HttpStatus.OK, null);
    }

}

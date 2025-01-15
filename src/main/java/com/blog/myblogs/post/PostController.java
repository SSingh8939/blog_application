package com.blog.myblogs.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/count")
    public ResponseEntity<Object> getPostsCount() {
        return postService.getAllPostCount();
    }

    @GetMapping
    public ResponseEntity<Object> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postService.getAllPosts(page - 1, size);
    }

    @GetMapping("/published/count")
    public ResponseEntity<Object> getPublishedPostsCount() {
        return postService.getAllPublishedPostCount();
    }

    @GetMapping("/published")
    public ResponseEntity<Object> getAllPublishedPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postService.getAllPublishedPosts(page - 1, size);

    }

    @GetMapping("/category/count/{id}")
    public ResponseEntity<Object> getPostsCountByCategory(@PathVariable long id) {
        return postService.getAllPostCountByCategory(id);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Object> getPostsByCategory(
            @PathVariable long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postService.getPostsByCategory(categoryId, page - 1, size);
    }

    @GetMapping("/count/admin/{id}")
    public ResponseEntity<Object> getPostsCountByAdmin(@PathVariable long id) {
        return postService.getAllPostCountByAdmin(id);
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<Object> getPostsByAdmin(
            @PathVariable long adminId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postService.getPostsByAdmin(adminId, page - 1, size);
    }

    @GetMapping("/search/count")
    public ResponseEntity<Object> getPostsCountByCategory(@RequestParam String keyword) {
        return postService.getAllPostCountBySearch(keyword);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postService.searchPosts(keyword, page - 1, size);
    }

    @PostMapping
    public ResponseEntity<Object> savePost(@RequestBody Post post) {
        return postService.savePost(post);

    }

    @PutMapping("/publish/{id}")
    public ResponseEntity<Object> saveAndPublishPost(@PathVariable long id) {
        return postService.saveAndPublishPost(id);

    }

    @PutMapping
    public ResponseEntity<Object> updatePost(@RequestBody Post post) {
        return postService.updatePost(post);

    }

    @PutMapping("/{postId}/like")
    public ResponseEntity<Object> likePost(@PathVariable long postId) {
        return postService.likePost(postId);
    }

    @PutMapping("/{postId}/undo-like")
    public ResponseEntity<Object> undoLike(@PathVariable long postId) {
        return postService.undoLike(postId);
    }

    @PutMapping("/{postId}/dislike")
    public ResponseEntity<Object> disLikePost(@PathVariable long postId) {
        return postService.disLikePost(postId);
    }

    @PutMapping("/{postId}/undo-dislike")
    public ResponseEntity<Object> undoDislike(@PathVariable long postId) {
        return postService.undoDislike(postId);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable long postId) {
        return postService.deletePost(postId);
    }
}

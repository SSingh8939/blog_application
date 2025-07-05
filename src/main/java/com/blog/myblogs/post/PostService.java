package com.blog.myblogs.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.blog.myblogs.exceptions.InvalidPaginationParameterException;
import com.blog.myblogs.exceptions.ResourceNotFoundException;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public long getAllPostCount() {
        return postRepository.count();
    }

    public List<Post> getAllPosts(int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAll(pageRequest).getContent();
    }

    public long getAllPublishedPostCount() {
        return postRepository.getPublishedCount();
    }

    public List<Post> getAllPublishedPosts(int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findByPublished(true, pageRequest).getContent();
    }

    public long getAllUnpublishedPostCount() {
        return postRepository.getUnpublishedCount();
    }

    public List<Post> getAllUnpublishedPosts(int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findByUnpublished(true, pageRequest).getContent();
    }

    public long getAllPostCountByCategory(long categoryId) {
        return postRepository.getCountByCategory(categoryId);
    }

    public List<Post> getPostsByCategory(long categoryId, int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAllByCategoryId(categoryId, pageRequest).getContent();
    }

    public long getAllPostCountByAdmin(long adminId) {
        return postRepository.getCountByAdmin(adminId);
    }

    public List<Post> getPostsByAdmin(long adminId, int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAllByAdminId(adminId, pageRequest).getContent();
    }

    public Post savePost(PostDTO dto) {
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .adminId(dto.getAdminId())
                .categoryId(dto.getCategoryId())
                .build();

        return postRepository.save(post.toBuilder().published(false).build());
    }

    public Post publish(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));

        Post updated = post.toBuilder()
                .published(true)
                .build();
        return postRepository.save(updated);
    }

    public Post unpublish(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));

        Post updated = post.toBuilder()
                .published(false)
                .build();
        return postRepository.save(updated);
    }

    public Post updatePost(Long id, PostDTO dto) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));

        Post updated = existing.toBuilder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .categoryId(dto.getCategoryId())
                .updatedAt(LocalDateTime.now())
                .build();

        return postRepository.save(updated);
    }

    private Post modifyPostLikesOrDislikes(long postId, boolean isLike, boolean isIncrement) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        long newLikes = existingPost.getLikes();
        long newDislikes = existingPost.getDisLikes();

        if (isLike) {
            newLikes = Math.max(0, newLikes + (isIncrement ? 1 : -1));
        } else {
            newDislikes = Math.max(0, newDislikes + (isIncrement ? 1 : -1));
        }

        Post updatedPost = existingPost.toBuilder()
                .likes(newLikes)
                .disLikes(newDislikes)
                .build();

        return postRepository.save(updatedPost);
    }

    public Post likePost(long postId) {
        return modifyPostLikesOrDislikes(postId, true, true);
    }

    public Post undoLike(long postId) {
        return modifyPostLikesOrDislikes(postId, true, false);
    }

    public Post disLikePost(long postId) {
        return modifyPostLikesOrDislikes(postId, false, true);
    }

    public Post undoDislike(long postId) {
        return modifyPostLikesOrDislikes(postId, false, false);
    }

    public void deletePost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    System.out.println("Throwing ResourceNotFoundException...");
                    return new ResourceNotFoundException("Post not found with ID: " + postId);
                });
        postRepository.delete(post);
    }

    public long getAllPostCountBySearch(String keyword) {
        return postRepository.getCountBySearch(keyword);
    }

    public List<Post> searchPosts(String keyword, int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.searchByKeyword(keyword, pageRequest).getContent();
    }

    private void validatePagination(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidPaginationParameterException();
        }
    }

}

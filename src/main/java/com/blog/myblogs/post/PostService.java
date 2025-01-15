package com.blog.myblogs.post;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.blog.myblogs.common.ResponseGenerator;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public ResponseEntity<Object> getAllPostCount() {
        try {
            long count = postRepository.count();
            return ResponseGenerator.generateResponse("Post count fetched successfully", HttpStatus.OK, count);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching post count", HttpStatus.INTERNAL_SERVER_ERROR,
                    null);
        }
    }

    public ResponseEntity<Object> getAllPosts(int page, int size) {
        try {
            Sort sort = Sort.by(Direction.DESC, "createdAt");
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Page<Post> pages = postRepository.findAll(pageRequest);
            List<Post> posts = pages.getContent();
            return ResponseGenerator.generateResponse("Posts fetched successfully", HttpStatus.OK, posts);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching posts", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<Object> getAllPublishedPostCount() {
        try {
            long count = postRepository.getPublishedCount();
            return ResponseGenerator.generateResponse("Post count fetched successfully", HttpStatus.OK, count);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching post count", HttpStatus.INTERNAL_SERVER_ERROR,
                    null);
        }
    }

    public ResponseEntity<Object> getAllPublishedPosts(int page, int size) {
        try {
            Sort sort = Sort.by(Direction.DESC, "createdAt");
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Page<Post> pages = postRepository.findByPublished(true, pageRequest);
            List<Post> posts = pages.getContent();
            return ResponseGenerator.generateResponse("Published posts fetched successfully", HttpStatus.OK, posts);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching published posts",
                    HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<Object> getAllPostCountByCategory(long categoryId) {
        try {
            long count = postRepository.getCountByCategory(categoryId);
            return ResponseGenerator.generateResponse("Post count fetched successfully", HttpStatus.OK, count);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching post count", HttpStatus.INTERNAL_SERVER_ERROR,
                    null);
        }
    }

    public ResponseEntity<Object> getPostsByCategory(long categoryId, int page, int size) {
        try {
            Sort sort = Sort.by(Direction.DESC, "createdAt");
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Page<Post> pages = postRepository.findAllByCategoryId(categoryId, pageRequest);
            List<Post> posts = pages.getContent();
            return ResponseGenerator.generateResponse("Posts by category fetched successfully", HttpStatus.OK, posts);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching posts by category",
                    HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<Object> getAllPostCountByAdmin(long adminId) {
        try {
            long count = postRepository.getCountByAdmin(adminId);
            return ResponseGenerator.generateResponse("Post count fetched successfully", HttpStatus.OK, count);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching post count", HttpStatus.INTERNAL_SERVER_ERROR,
                    null);
        }
    }

    public ResponseEntity<Object> getPostsByAdmin(long adminId, int page, int size) {
        try {
            Sort sort = Sort.by(Direction.DESC, "createdAt");
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Page<Post> pages = postRepository.findAllByAdmin(adminId, pageRequest);
            List<Post> posts = pages.getContent();
            return ResponseGenerator.generateResponse("Posts by Admin fetched successfully", HttpStatus.OK, posts);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching posts by Admin",
                    HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<Object> savePost(Post post) {
        try {
            Post savedPost = postRepository.save(post);
            return ResponseGenerator.generateResponse("Post saved successfully", HttpStatus.CREATED, savedPost);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error saving post", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<Object> saveAndPublishPost(long id) {
        try {
            Optional<Post> optionalPost = postRepository.findById(id);
            if (optionalPost.isPresent()) {
                Post existingPost = optionalPost.get();
                existingPost.setPublished(true);
                Post savedPost = postRepository.save(existingPost);
                return ResponseGenerator.generateResponse("Post published successfully", HttpStatus.OK, savedPost);
            } else {
                return ResponseGenerator.generateResponse("Post not found", HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error publishing post", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<Object> updatePost(Post post) {
        try {
            Optional<Post> optionalPost = postRepository.findById(post.getId());
            if (optionalPost.isPresent()) {
                Post existingPost = optionalPost.get();
                existingPost.setTitle(post.getTitle());
                existingPost.setContent(post.getContent());
                existingPost.setAdminId(post.getAdminId());
                existingPost.setCategoryId(post.getCategoryId());
                existingPost.setLikes(post.getLikes());
                existingPost.setDisLikes(post.getDisLikes());
                Post updatedPost = postRepository.save(existingPost);
                return ResponseGenerator.generateResponse("Post updated successfully", HttpStatus.OK, updatedPost);
            } else {
                return ResponseGenerator.generateResponse("Post not found", HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error updating post", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private ResponseEntity<Object> modifyPostLikesOrDislikes(long postId, boolean isLike, boolean isIncrement) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Post existingPost = optionalPost.get();
                if (isLike) {
                    existingPost.setLikes(Math.max(0, existingPost.getLikes() + (isIncrement ? 1 : -1)));
                } else {
                    existingPost.setDisLikes(Math.max(0, existingPost.getDisLikes() + (isIncrement ? 1 : -1)));
                }
                Post updatedPost = postRepository.save(existingPost);
                return ResponseGenerator.generateResponse("Post updated successfully", HttpStatus.OK, updatedPost);
            } else {
                return ResponseGenerator.generateResponse("Post not found", HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error updating post", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<Object> likePost(long postId) {
        return modifyPostLikesOrDislikes(postId, true, true);
    }

    public ResponseEntity<Object> undoLike(long postId) {
        return modifyPostLikesOrDislikes(postId, true, false);
    }

    public ResponseEntity<Object> disLikePost(long postId) {
        return modifyPostLikesOrDislikes(postId, false, true);
    }

    public ResponseEntity<Object> undoDislike(long postId) {
        return modifyPostLikesOrDislikes(postId, false, false);
    }

    public ResponseEntity<Object> deletePost(long postId) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                postRepository.deleteById(postId);
                return ResponseGenerator.generateResponse("Post deleted successfully", HttpStatus.OK, null);
            } else {
                return ResponseGenerator.generateResponse("Post not found", HttpStatus.NOT_FOUND, null);
            }
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error deleting post", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<Object> getAllPostCountBySearch(String keyword) {
        try {
            long count = postRepository.getCountBySearch(keyword);
            return ResponseGenerator.generateResponse("Post count fetched successfully", HttpStatus.OK, count);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error fetching post count", HttpStatus.INTERNAL_SERVER_ERROR,
                    null);
        }
    }

    public ResponseEntity<Object> searchPosts(String keyword, int page, int size) {
        try {
            Sort sort = Sort.by(Direction.DESC, "createdAt");
            PageRequest pageRequest = PageRequest.of(page, size, sort);
            Page<Post> pages = postRepository.searchByKeyword(keyword, pageRequest);
            List<Post> posts = pages.getContent();
            return ResponseGenerator.generateResponse("Posts fetched successfully", HttpStatus.OK, posts);
        } catch (Exception e) {
            return ResponseGenerator.generateResponse("Error searching posts", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}

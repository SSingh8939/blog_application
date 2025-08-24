package com.blog.myblogs.post;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.myblogs.category.Category;
import com.blog.myblogs.category.CategoryRepository;
import com.blog.myblogs.comment.Comment;
import com.blog.myblogs.comment.CommentService;
import com.blog.myblogs.exceptions.InvalidPaginationParameterException;
import com.blog.myblogs.exceptions.ResourceNotFoundException;
import com.blog.myblogs.user.User;
import com.blog.myblogs.user.UserRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public long getAllPostCount() {
        return postRepository.count();
    }

    public List<Post> getPostsWithComment(List<Post> posts) {
        posts.forEach(post -> {
            List<Comment> comments = commentService.getAllCommentsWithRepliesByPostId(post.getId());

            post.setComments(comments);
        });

        return posts;
    }

    public Post getSinglePostWithComment(Post post) {
        List<Comment> comments = commentService.getAllCommentsWithRepliesByPostId(post.getId());
        post.setComments(comments);
        return post;
    }

    public List<Post> getAllPosts(int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> posts = postRepository.findAll(pageRequest).getContent();

        return getPostsWithComment(posts);
    }

    public long getAllPublishedPostCount() {
        return postRepository.getPublishedCount();
    }

    public List<Post> getAllPublishedPosts(int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> posts = postRepository.findByPublished(true, pageRequest).getContent();

        return getPostsWithComment(posts);
    }

    public long getAllUnpublishedPostCount() {
        return postRepository.getUnpublishedCount();
    }

    public List<Post> getAllUnpublishedPosts(int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> posts = postRepository.findByUnpublished(true, pageRequest).getContent();

        return getPostsWithComment(posts);
    }

    public long getAllPostCountByCategory(long categoryId) {
        return postRepository.getCountByCategory(categoryId);
    }

    public List<Post> getPostsByCategory(long categoryId, int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> posts = postRepository.findAllByCategory_Id(categoryId, pageRequest).getContent();

        return getPostsWithComment(posts);
    }

    public long getAllPostCountByAdmin(long adminId) {
        return postRepository.getCountByAdmin(adminId);
    }

    public List<Post> getPostsByAdmin(long adminId, int page, int size) {
        validatePagination(page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> posts = postRepository.findAllByAuthor_Id(adminId, pageRequest).getContent();

        return getPostsWithComment(posts);
    }

    public Post savePost(PostDTO dto, MultipartFile image) {
        User author = userRepository.findById(dto.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getAdminId()));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
        String imageUrl = saveImage(image, null);
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .category(category)
                .imageUrl(imageUrl)
                .published(false)
                .build();

        Post p = postRepository.save(post);

        return getSinglePostWithComment(p);
    }

    public String saveImage(MultipartFile image, String existingPath) {
        String fileDir = "images";
        String filePath = fileDir + File.separator + System.currentTimeMillis() + image.getOriginalFilename();

        if (image.isEmpty() || image.getOriginalFilename() == null) {
            return "";
        }

        try {
            if (existingPath != null && !existingPath.isEmpty()) {
                File oldFile = new File(existingPath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            File f = new File(fileDir);
            if (!f.exists()) {
                f.mkdirs();
            }
            image.transferTo(new File(filePath));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save image");
        }

        return filePath;
    }

    public Post publish(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));

        Post updated = post.toBuilder()
                .published(true)
                .build();
        Post p = postRepository.save(updated);

        return getSinglePostWithComment(p);
    }

    public Post unpublish(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));

        Post updated = post.toBuilder()
                .published(false)
                .build();
        Post p = postRepository.save(updated);

        return getSinglePostWithComment(p);
    }

    public Post updatePost(Long id, PostDTO dto, MultipartFile image) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        String imageUrl = saveImage(image, existing.getImageUrl());

        Post updated = existing.toBuilder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(category)
                .updatedAt(LocalDateTime.now())
                .imageUrl(imageUrl)
                .build();

        Post p = postRepository.save(updated);

        return getSinglePostWithComment(p);
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

        Post p = postRepository.save(updatedPost);

        return getSinglePostWithComment(p);
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
        List<Post> posts = postRepository.searchByKeyword(keyword, pageRequest).getContent();

        return getPostsWithComment(posts);
    }

    private void validatePagination(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new InvalidPaginationParameterException();
        }
    }

}

package com.blog.myblogs.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

        @Query("SELECT COUNT(p) from Post p WHERE p.published = true")
        long getPublishedCount();

        @Query("SELECT COUNT(p) from Post p WHERE p.published = false")
        long getUnpublishedCount();

        Page<Post> findByPublished(boolean published, PageRequest page);

        @Query("SELECT p From Post p WHERE p.published = false")
        Page<Post> findByUnpublished(boolean published, PageRequest page);

        @Query("SELECT COUNT(p) from Post p WHERE p.category.id = :categoryId")
        long getCountByCategory(long categoryId);

        Page<Post> findAllByCategory_Id(long categoryId, PageRequest page);

        @Query("SELECT COUNT(p) FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        long getCountBySearch(String keyword);

        @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<Post> searchByKeyword(String keyword, PageRequest page);

        @Query("SELECT COUNT(p) from Post p WHERE p.author.id = :adminId")
        long getCountByAdmin(long adminId);

        Page<Post> findAllByAuthor_Id(long authorId, PageRequest page);

}

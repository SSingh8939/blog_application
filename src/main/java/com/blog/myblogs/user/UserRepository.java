package com.blog.myblogs.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<User> findByVerificationToken(String token);

    Optional<User> findByResetToken(String token);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword% OR u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword%")
    Page<User> searchUsers(String keyword, Pageable pageable);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}

package com.complete.todayspace.domain.post.repository;

import com.complete.todayspace.domain.post.entitiy.Post;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByIdAndUserId(Long postId, Long userId);

    Optional<Post> findByIdAndUserId(Long postId, Long userId);
}

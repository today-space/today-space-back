package com.complete.todayspace.domain.post.repository;

import com.complete.todayspace.domain.post.dto.PostMainResponseDto;
import com.complete.todayspace.domain.post.dto.PostResponseDto;
import com.complete.todayspace.domain.post.entitiy.Post;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PostRepository extends JpaRepository<Post, Long>,
    QuerydslPredicateExecutor<PostResponseDto>, PostRepositoryQuery{

    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByIdAndUserId(Long postId, Long userId);

    Optional<Post> findByIdAndUserId(Long postId, Long userId);
}

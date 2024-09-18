package com.complete.todayspace.domain.post.repository;

import com.complete.todayspace.domain.post.dto.PostResponseDto;
import com.complete.todayspace.domain.post.entitiy.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>,
    QuerydslPredicateExecutor<PostResponseDto>, PostRepositoryQuery{

    boolean existsByIdAndUserId(Long postId, Long userId);

    Optional<Post> findByIdAndUserId(Long postId, Long userId);
}

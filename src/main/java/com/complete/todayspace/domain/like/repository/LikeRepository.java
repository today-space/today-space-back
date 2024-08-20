package com.complete.todayspace.domain.like.repository;

import com.complete.todayspace.domain.like.entity.Like;
import com.complete.todayspace.domain.post.entitiy.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>,
        QuerydslPredicateExecutor<Post>, LikeRepositoryQuery {

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    long countByPostId(Long postId);

    List<Like> findByPostId(Long postId);
}

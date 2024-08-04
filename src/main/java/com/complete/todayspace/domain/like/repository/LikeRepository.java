package com.complete.todayspace.domain.like.repository;

import com.complete.todayspace.domain.like.entity.Like;
import com.complete.todayspace.domain.post.dto.PostMainResponseDto;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface LikeRepository extends JpaRepository<Like, Long>,
    QuerydslPredicateExecutor<PostMainResponseDto>, LikeRepositoryQuery {

    Optional<Like> findByUserIdAndPostId(Long id, Long postId);
    long countByPostId(Long postId);

    List<Like> findByPostId(Long postId);
}

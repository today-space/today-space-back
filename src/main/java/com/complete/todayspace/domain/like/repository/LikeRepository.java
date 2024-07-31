package com.complete.todayspace.domain.like.repository;

import com.complete.todayspace.domain.like.entity.Like;
import com.complete.todayspace.domain.post.entitiy.Post;
import com.complete.todayspace.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndPostId(Long id, Long postId);
    long countByPostId(Long postId);
}

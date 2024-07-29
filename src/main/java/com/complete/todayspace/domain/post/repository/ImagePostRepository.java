package com.complete.todayspace.domain.post.repository;

import com.complete.todayspace.domain.post.entitiy.ImagePost;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagePostRepository extends JpaRepository<ImagePost, Long> {
    List<ImagePost> findByPostId(Long postId);

    List<ImagePost> findByPostIdIn(List<Long> postIds);
}

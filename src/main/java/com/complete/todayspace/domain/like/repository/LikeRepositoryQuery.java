package com.complete.todayspace.domain.like.repository;

import com.complete.todayspace.domain.post.entitiy.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeRepositoryQuery {

    Page<Post> findTopLikedPosts(Pageable pageable);
}

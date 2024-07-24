package com.complete.todayspace.domain.post.repository;

import com.complete.todayspace.domain.post.entitiy.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}

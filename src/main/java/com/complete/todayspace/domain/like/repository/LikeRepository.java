package com.complete.todayspace.domain.like.repository;

import com.complete.todayspace.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}

package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.wish.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {
}

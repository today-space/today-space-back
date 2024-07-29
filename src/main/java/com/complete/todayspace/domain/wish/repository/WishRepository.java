package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.wish.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productsId);

    Page<Wish> findByUserId(Long userId, Pageable pageable);

}


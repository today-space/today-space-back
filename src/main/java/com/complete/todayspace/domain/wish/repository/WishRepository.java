package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.wish.entity.Wish;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByUserIdAndProductId(Long userId, Long productsId);

    Page<Wish> findByUserId(Long userId, Pageable pageable);

}


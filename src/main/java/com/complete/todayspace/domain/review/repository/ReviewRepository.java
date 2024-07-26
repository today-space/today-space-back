package com.complete.todayspace.domain.review.repository;

import com.complete.todayspace.domain.review.entity.Review;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Object> findByUserIdAndProductId(Long id, Long productsId);

    Page<Review> findAllByProductIdIn(List<Long> productId, Pageable pageable);

}

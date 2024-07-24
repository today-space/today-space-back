package com.complete.todayspace.domain.review.repository;

import com.complete.todayspace.domain.review.entity.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Object> findByUserIdAndProductId(Long id, Long productsId);
}

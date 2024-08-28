package com.complete.todayspace.domain.review.repository;

import com.complete.todayspace.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryQuery {

    Optional<Object> findByUserIdAndProductId(Long id, Long productsId);

}

package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.product.dto.ProductImageResponseDto;
import com.complete.todayspace.domain.wish.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long>,
    QuerydslPredicateExecutor<ProductImageResponseDto>, WishRepositoryQuery {

    Optional<Wish> findByUserIdAndProductId(Long userId, Long productsId);

}


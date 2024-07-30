package com.complete.todayspace.domain.product.repository;

import com.complete.todayspace.domain.product.entity.ImageProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageProductRepository extends JpaRepository<ImageProduct, Long> {

    List<ImageProduct> findByProductId(Long productId);

    List<ImageProduct> findByProductIdIn(List<Long> productIds);

    List<ImageProduct> findByProductIdOrderByCreatedAtAsc(Long productId);

}

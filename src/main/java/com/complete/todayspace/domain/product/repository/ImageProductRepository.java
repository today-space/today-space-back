package com.complete.todayspace.domain.product.repository;

import com.complete.todayspace.domain.product.entity.ImageProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageProductRepository extends JpaRepository<ImageProduct, Long> {

    List<ImageProduct> findByProductId(Long productId);

    List<ImageProduct> findByProductIdIn(List<Long> productIds);
}

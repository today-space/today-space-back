package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishRepositoryQuery {

    public Page<Product> findTopWishedProducts(Pageable pageable);
}

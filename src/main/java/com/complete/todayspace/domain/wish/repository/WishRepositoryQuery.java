package com.complete.todayspace.domain.wish.repository;

import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishRepositoryQuery {

    Page<Product> findTopWishedProducts(Pageable pageable);

    Page<ProductResponseDto> findMyWishList(Long userId, Pageable pageable);

}

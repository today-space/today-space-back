package com.complete.todayspace.domain.product.repository;

import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryQuery {

    Page<ProductResponseDto> findMyProductList(Long userId, Pageable pageable);

}

package com.complete.todayspace.domain.product.repository;

import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.domain.product.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryQuery {
  
    Page<ProductResponseDto> findMyProductList(Long userId, Pageable pageable);
  
    Page<ProductResponseDto> findProductsByTitleLike(Pageable pageable, String search);
    Page<ProductResponseDto> findByTitleContainingIgnoreCaseAndAddress(Pageable pageable, String search, Address address);
    Page<ProductResponseDto> findByTitleContainingIgnoreCase(Pageable pageable, String search);

    Page<ProductResponseDto> findProducts(Pageable pageable);
    Page<ProductResponseDto> findAllByAddress(Pageable pageable, Address address);

}

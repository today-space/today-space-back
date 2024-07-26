package com.complete.todayspace.domain.product.repository;

import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByIdAndUserId(Long productId, Long userId);

    Page<Product> findAllByAddress(Pageable pageable, Address address);

    Page<Product> findProductsByTitleLike(Pageable pageable, String search);

    Page<Product> findByTitleContainingIgnoreCaseAndAddress(Pageable pageable, String search, Address address);

    Page<Product> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Product> findAllByUserUsername(String username);

    List<Product> findAllByUserId(Long id);

}

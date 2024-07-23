package com.complete.todayspace.domain.product.service;

import com.complete.todayspace.domain.product.dto.CreateProductRequestDto;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.repository.ProductRepository;
import com.complete.todayspace.domain.user.entity.User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(User user, CreateProductRequestDto requestDto) {
        Product saveProduct = new Product(requestDto.getTitle(), requestDto.getPrice(), requestDto.getContent(),
            requestDto.getAddress(), requestDto.getState(), user);

        productRepository.save(saveProduct);
    }
}

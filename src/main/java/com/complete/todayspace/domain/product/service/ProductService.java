package com.complete.todayspace.domain.product.service;

import com.complete.todayspace.domain.product.dto.CreateProductRequestDto;
import com.complete.todayspace.domain.product.dto.EditProductRequestDto;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.repository.ProductRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void createProduct(User user, CreateProductRequestDto requestDto) {
        Product saveProduct = new Product(requestDto.getTitle(), requestDto.getPrice(), requestDto.getContent(),
            requestDto.getAddress(), requestDto.getState(), user);

        productRepository.save(saveProduct);
    }

    @Transactional
    public void editProduct(Long id, Long productsId, EditProductRequestDto requestDto) {
        if(!isProductOwner(productsId,id)){
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }
        Product product = findByProduct(productsId);
        product.updateProduct(requestDto.getPrice(), requestDto.getTitle(), requestDto.getContent(),
            requestDto.getAddress(), requestDto.getState());
    }

    public Product findByProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(
            () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }

    private boolean isProductOwner(Long productId, Long userId) {
        return productRepository.existsByIdAndUserId(productId,userId);
    }
}


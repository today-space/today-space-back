package com.complete.todayspace.domain.product.service;

import com.complete.todayspace.domain.product.dto.CreateProductRequestDto;
import com.complete.todayspace.domain.product.dto.EditProductRequestDto;
import com.complete.todayspace.domain.product.dto.ProductDetailResponseDto;
import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.domain.product.entity.Product;
import com.complete.todayspace.domain.product.repository.ProductRepository;
import com.complete.todayspace.domain.user.entity.User;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void createProduct(User user, CreateProductRequestDto requestDto) {

        Product saveProduct = new Product(requestDto.getTitle(), requestDto.getPrice(),
            requestDto.getContent(),
            requestDto.getAddress(), requestDto.getState(), user);

        productRepository.save(saveProduct);
    }

    @Transactional
    public void editProduct(Long id, Long productsId, EditProductRequestDto requestDto) {

        Product product = findByProduct(productsId);
        if (!isProductOwner(productsId, id)) {
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }
        product.updateProduct(requestDto.getPrice(), requestDto.getTitle(), requestDto.getContent(),
            requestDto.getAddress(), requestDto.getState());
    }

    @Transactional
    public void deleteProduct(Long id, Long productsId) {

        Product product = findByProduct(productsId);
        if (!isProductOwner(productsId, id)) {
            throw new CustomException(ErrorCode.NOT_OWNER_PRODUCT);
        }
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProduct(Long productsId) {

        Product product = findByProduct(productsId);
        return new ProductDetailResponseDto(product.getId(), product.getUser().getUsername(),
            product.getPrice(), product.getTitle(), product.getContent(), product.getAddress(),
            product.getState(), product.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductSearch(Pageable pageable, String search) {

        Page<Product> Page = productRepository.findProductsByTitleLike(pageable, search);
        if (Page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        return Page.map(product -> new ProductResponseDto(
            product.getId(),
            product.getPrice(),
            product.getTitle()
        ));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductRegion(Pageable pageable, String address) {

        if (!isAddressValid(address)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        Page<Product> page = productRepository.findAllByAddress(pageable, Address.valueOf(address));
        return page.map(product -> new ProductResponseDto(
            product.getId(),
            product.getPrice(),
            product.getTitle()
        ));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductSearchRegion(Pageable pageable, String search,
        String region) {
        Page<Product> Page;
        Page = productRepository.findByTitleContainingIgnoreCaseAndAddress(pageable, search,
            Address.valueOf(region));
        if (Page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        return Page.map(product -> new ProductResponseDto(
            product.getId(),
            product.getPrice(),
            product.getTitle()
        ));
    }


    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductPage(Pageable pageable) {

        Page<Product> Page = productRepository.findAll(pageable);
        if (Page.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        return Page.map(product -> new ProductResponseDto(
            product.getId(),
            product.getPrice(),
            product.getTitle()
        ));
    }

    private boolean isAddressValid(String address) {
        for (Address addressString : Address.values()) {
            if (addressString.name().equalsIgnoreCase(address)) {
                return true;
            }
        }
        return false;
    }

    public Product findByProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(
            () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }

    private boolean isProductOwner(Long productId, Long userId) {
        return productRepository.existsByIdAndUserId(productId, userId);
    }

}


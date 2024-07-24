package com.complete.todayspace.domain.product.controller;

import com.complete.todayspace.domain.product.dto.EditProductRequestDto;
import com.complete.todayspace.domain.product.dto.ProductDetailResponseDto;
import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.global.dto.DataResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.complete.todayspace.domain.product.dto.CreateProductRequestDto;
import com.complete.todayspace.domain.product.service.ProductService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<StatusResponseDto> createProduct(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody CreateProductRequestDto requestDto
    ) {
        productService.createProduct(userDetails.getUser(), requestDto);
        StatusResponseDto response = new StatusResponseDto(SuccessCode.PRODUCTS_CREATE);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/products/{productsId}")
    public ResponseEntity<StatusResponseDto> editProduct(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long productsId,
        @Valid @RequestBody EditProductRequestDto requestDto
    ) {
        productService.editProduct(userDetails.getUser().getId(), productsId, requestDto);
        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.PRODUCTS_UPDATE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productsId}")
    public ResponseEntity<StatusResponseDto> deleteProduct(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long productsId
    ) {
        productService.deleteProduct(userDetails.getUser().getId(), productsId);
        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.PRODUCTS_DELETE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/products/{productsId}")
    public ResponseEntity<DataResponseDto<ProductDetailResponseDto>> getProduct(
        @PathVariable Long productsId
    ) {
        ProductDetailResponseDto responseDto = productService.getProduct(productsId);
        DataResponseDto<ProductDetailResponseDto> product = new DataResponseDto<>(
            SuccessCode.POSTS_GET, responseDto);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<DataResponseDto<Page<ProductResponseDto>>> getProductPage(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        @RequestParam(defaultValue = "all") String A,
        @RequestParam(required = false) String B

    ) {
        Page<ProductResponseDto> responseDto;

        if (A.equals("search")) {
            responseDto = productService.getProductSearch(pageable, B);
        } else if (A.equals("address")) {
            responseDto = productService.getProductAddress(pageable, Address.valueOf(B));
        } else {
            responseDto = productService.getProductPage(pageable);
        }

        DataResponseDto<Page<ProductResponseDto>> product = new DataResponseDto<>(
            SuccessCode.POSTS_GET, responseDto);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}


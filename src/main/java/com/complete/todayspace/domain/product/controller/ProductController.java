package com.complete.todayspace.domain.product.controller;

import com.complete.todayspace.domain.product.dto.EditProductRequestDto;
import com.complete.todayspace.domain.product.dto.ProductResponseDto;
import com.complete.todayspace.global.dto.DataResponseDto;
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
    public ResponseEntity<DataResponseDto<ProductResponseDto>> getProduct(
        @PathVariable Long productsId
    ) {
        ProductResponseDto responseDto = productService.getProduct(productsId);
        DataResponseDto<ProductResponseDto> product = new DataResponseDto<>(200,
            SuccessCode.POSTS_GET.getMessage(), responseDto);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}

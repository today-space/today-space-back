package com.complete.todayspace.domain.product.controller;

import com.complete.todayspace.domain.product.dto.*;
import com.complete.todayspace.global.dto.DataResponseDto;
import com.complete.todayspace.global.valid.PageValidation;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PutMapping("/products/{productId}")
    public ResponseEntity<StatusResponseDto> editProduct(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long productId,
        @Valid @RequestBody EditProductRequestDto requestDto
    ) {
        productService.editProduct(userDetails.getUser().getId(), productId, requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.PRODUCTS_UPDATE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PatchMapping("/products/{productId}/up")
    public ResponseEntity<StatusResponseDto> updateProduct(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long productId
    ) {
        productService.updateProduct(userDetails.getUser().getId(), productId);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.PRODUCTS_UPDATE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<StatusResponseDto> deleteProduct(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long productId
    ) {
        productService.deleteProduct(userDetails.getUser().getId(), productId);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.PRODUCTS_DELETE);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<DataResponseDto<ProductDetailResponseDto>> getProduct(
        @PathVariable Long productId
    ) {
        ProductDetailResponseDto responseDto = productService.getProduct(productId);

        DataResponseDto<ProductDetailResponseDto> product = new DataResponseDto<>(
            SuccessCode.PRODUCTS_GET, responseDto);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<DataResponseDto<Page<ProductResponseDto>>> getProductPage(
        @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
        @ModelAttribute PageParamDto pageParamDto
    ) {

        int pageNumber = PageValidation.pageValidationGetProduct(pageParamDto.getPage());
        pageable = PageRequest.of(pageNumber - 1, pageable.getPageSize(), pageable.getSort());

        Page<ProductResponseDto> responseDto = productService.getResponseDto(pageable, pageParamDto);

        DataResponseDto<Page<ProductResponseDto>> product = new DataResponseDto<>(
            SuccessCode.PRODUCTS_GET, responseDto);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/my/products")
    public ResponseEntity<DataResponseDto<Page<ProductResponseDto>>> getMyProductList(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam Map<String, String> params
    ) {

        int page = PageValidation.pageValidationInParams(params);

        Page<ProductResponseDto> responseDto = productService.getMyProductList(
            userDetails.getUser().getId(), page - 1);

        DataResponseDto<Page<ProductResponseDto>> dataResponseDto = new DataResponseDto<>(
            SuccessCode.PRODUCTS_GET, responseDto);

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }
}


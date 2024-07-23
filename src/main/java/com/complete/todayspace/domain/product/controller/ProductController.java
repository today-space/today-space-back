package com.complete.todayspace.domain.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
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
	){
		productService.createProduct(userDetails.getUser(),requestDto);
		StatusResponseDto response = new StatusResponseDto (SuccessCode.PRODUCTS_CREATE);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}

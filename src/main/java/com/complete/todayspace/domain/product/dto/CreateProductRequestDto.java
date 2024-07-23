package com.complete.todayspace.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateProductRequestDto {
	@NotBlank
	private String title;

	@NotNull
	private Long price;

	@NotBlank
	private String content;

	private String address;

	private String state;
}


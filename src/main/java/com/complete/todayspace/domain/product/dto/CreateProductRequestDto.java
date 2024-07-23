package com.complete.todayspace.domain.product.dto;

import com.complete.todayspace.domain.product.entity.Address;
import com.complete.todayspace.domain.product.entity.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CreateProductRequestDto {
	@NotBlank
	@Length(max = 60)
	private String title;

	@NotNull
	private Long price;

	@NotBlank
	@Length(max = 600)
	private String content;

	private Address address;

	private State state;
}


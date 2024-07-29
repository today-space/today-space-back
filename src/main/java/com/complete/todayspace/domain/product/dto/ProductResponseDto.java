package com.complete.todayspace.domain.product.dto;

import lombok.Getter;

@Getter
public class ProductResponseDto {

    private final Long id;
    private final Long price;
    private final String title;

    public ProductResponseDto(Long id, Long price, String title) {
        this.id = id;
        this.price = price;
        this.title = title;
    }
}

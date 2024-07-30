package com.complete.todayspace.domain.product.dto;

import lombok.Getter;

@Getter
public class ProductResponseDto {

    private final Long id;
    private final Long price;
    private final String title;
    private final String imagePath;

    public ProductResponseDto(Long id, Long price, String title, String imagePath) {
        this.id = id;
        this.price = price;
        this.title = title;
        this.imagePath = imagePath;
    }

}

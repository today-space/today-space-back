package com.complete.todayspace.domain.product.dto;

import lombok.Getter;

@Getter
public class ProductImageResponseDto {

    private final Long id;
    private final Long price;
    private final String title;
    private final ImageDto firstImage;

    public ProductImageResponseDto(Long id, Long price, String title,
        ImageDto firstImage) {
        this.id = id;
        this.price = price;
        this.title = title;
        this.firstImage = firstImage;
    }
}

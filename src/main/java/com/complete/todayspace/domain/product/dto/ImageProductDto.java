package com.complete.todayspace.domain.product.dto;

import lombok.Getter;

@Getter
public class ImageProductDto {

    private final Long id;
    private final String imagePath;

    public ImageProductDto(Long id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }
}

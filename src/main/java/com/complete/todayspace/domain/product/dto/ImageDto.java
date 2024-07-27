package com.complete.todayspace.domain.product.dto;

import lombok.Getter;

@Getter
public class ImageDto {

    private final Long id;
    private final String url;

    public ImageDto(Long id, String url) {
        this.id = id;
        this.url = url;
    }
}

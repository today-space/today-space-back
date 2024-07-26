package com.complete.todayspace.domain.product.dto;

import lombok.Getter;

@Getter
public class ImageDto {

    private final String id;
    private final String url;

    public ImageDto(String id, String url) {
        this.id = id;
        this.url = url;
    }
}

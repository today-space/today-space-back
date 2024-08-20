package com.complete.todayspace.domain.product.dto;

import lombok.Getter;

@Getter
public class PageParamDto {

    private final String page;
    private final String search;
    private final String region;
    private final Boolean topWished;

    public PageParamDto (String page, String search, String region, Boolean topWished) {

        this.page = page;
        this.search = search;
        this.region = region;
        this.topWished = topWished;
    }
}

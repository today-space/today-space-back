package com.complete.todayspace.domain.post.dto;

import lombok.Getter;

@Getter
public class PostImageDto {

    private final Long id;
    private final String url;

    public PostImageDto(Long id, String url) {
        this.id = id;
        this.url = url;
    }
}

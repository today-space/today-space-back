package com.complete.todayspace.domain.post.dto;

import lombok.Getter;

@Getter
public class PostImageResponseDto {

    private final Long id;
    private final String content;
    private final PostImageDto firstImage;

    public PostImageResponseDto(Long id, String content, PostImageDto firstImage) {
        this.id = id;
        this.content = content;
        this.firstImage = firstImage;
    }
}

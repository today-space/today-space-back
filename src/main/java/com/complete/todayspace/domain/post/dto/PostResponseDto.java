package com.complete.todayspace.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private final Long id;
    private final String content;
    private final LocalDateTime updatedAt;
    private final List<PostImageDto> images;

    public PostResponseDto(Long id, String content, LocalDateTime updatedAt, List<PostImageDto> images) {
        this.id = id;
        this.content = content;
        this.updatedAt = updatedAt;
        this.images = images;
    }
}

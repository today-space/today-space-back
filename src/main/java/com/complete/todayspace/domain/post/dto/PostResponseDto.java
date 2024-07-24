package com.complete.todayspace.domain.post.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private final Long id;
    private final String content;
    private final LocalDateTime updatedAt;

    public PostResponseDto(Long id, String content, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.updatedAt = updatedAt;
    }
}

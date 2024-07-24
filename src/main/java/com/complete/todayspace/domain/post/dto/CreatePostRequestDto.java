package com.complete.todayspace.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreatePostRequestDto {
    @NotBlank
    @Size(max = 600)
    private String content;
}

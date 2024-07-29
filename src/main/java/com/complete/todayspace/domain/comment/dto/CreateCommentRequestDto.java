package com.complete.todayspace.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateCommentRequestDto {

    @NotBlank
    @Size(max = 300)
    private String content;
}

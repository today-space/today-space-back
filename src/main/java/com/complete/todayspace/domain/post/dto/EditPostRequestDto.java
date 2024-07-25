package com.complete.todayspace.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class EditPostRequestDto {

    @NotBlank
    @Length(max = 600)
    private String content;
}

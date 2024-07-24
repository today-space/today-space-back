package com.complete.todayspace.domain.review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class ReviewRequestDto {

    @NotBlank
    @Length(max = 300)
    private String content;
}

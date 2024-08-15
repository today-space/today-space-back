package com.complete.todayspace.domain.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OAuthRequestDto {

    @NotBlank
    private String code;

}

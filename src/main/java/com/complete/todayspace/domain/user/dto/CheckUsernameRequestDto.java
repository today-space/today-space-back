package com.complete.todayspace.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CheckUsernameRequestDto {

    @NotBlank
    private String username;

}

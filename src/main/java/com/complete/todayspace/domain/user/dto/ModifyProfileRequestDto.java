package com.complete.todayspace.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ModifyProfileRequestDto {

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 4, max = 20)
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*]+$")
    private String newPassword;

    @NotBlank
    private String checkPassword;

}

package com.complete.todayspace.domain.user.dto;

import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private String username;

    public ProfileResponseDto(String username) {
        this.username = username;
    }

}

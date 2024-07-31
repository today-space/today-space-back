package com.complete.todayspace.domain.user.dto;

import lombok.Getter;

@Getter
public class ProfileResponseDto {

    private String username;
    private String imagePath;

    public ProfileResponseDto(String username, String imagePath) {
        this.username = username;
        this.imagePath = imagePath;
    }

}

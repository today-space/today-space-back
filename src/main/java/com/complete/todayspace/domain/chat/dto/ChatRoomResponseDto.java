package com.complete.todayspace.domain.chat.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ChatRoomResponseDto {

    private final String roomId;
    private final String username;
    private final String profileImage;

    @QueryProjection
    public ChatRoomResponseDto(String roomId, String username, String profileImage) {
        this.roomId = roomId;
        this.username = username;
        this.profileImage = profileImage;
    }

}

package com.complete.todayspace.domain.chat.dto;

import lombok.Getter;

@Getter
public class ChatMessageRequestDto {

    private String roomId;
    private String sender;
    private String message;

}

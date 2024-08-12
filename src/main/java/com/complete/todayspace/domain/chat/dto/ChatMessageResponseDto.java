package com.complete.todayspace.domain.chat.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponseDto {

    private final String sender;
    private final String message;
    private final LocalDateTime sendDate;

    public ChatMessageResponseDto(String sender, String message, LocalDateTime sendDate) {
        this.sender = sender;
        this.message = message;
        this.sendDate = sendDate;
    }

}

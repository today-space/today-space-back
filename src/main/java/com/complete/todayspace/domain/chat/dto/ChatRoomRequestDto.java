package com.complete.todayspace.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChatRoomRequestDto {

    @NotBlank
    private String roomId;

    @NotNull
    private Long seller;

    @NotNull
    private Long buyer;

}

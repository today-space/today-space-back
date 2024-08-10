package com.complete.todayspace.domain.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChatRoomRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    private Long seller;

}

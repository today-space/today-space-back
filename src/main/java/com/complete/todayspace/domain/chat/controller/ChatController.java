package com.complete.todayspace.domain.chat.controller;

import com.complete.todayspace.domain.chat.dto.ChatRoomRequestDto;
import com.complete.todayspace.domain.chat.service.ChatService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chatroom")
    public ResponseEntity<StatusResponseDto> createChatRoom(@Valid @RequestBody ChatRoomRequestDto requestDto) {

        chatService.createChatRoom(requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.CHAT_ROOM_CREATE);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}

package com.complete.todayspace.domain.chat.controller;

import com.complete.todayspace.domain.chat.dto.ChatRoomRequestDto;
import com.complete.todayspace.domain.chat.service.ChatService;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<StatusResponseDto> enterChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChatRoomRequestDto requestDto
    ) {

        chatService.enterChatRoom(userDetails.getUser().getId(), requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.ENTER_CHAT_ROOM);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}

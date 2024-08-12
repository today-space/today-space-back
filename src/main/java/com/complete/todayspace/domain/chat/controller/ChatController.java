package com.complete.todayspace.domain.chat.controller;

import com.complete.todayspace.domain.chat.dto.ChatMessageRequestDto;
import com.complete.todayspace.domain.chat.dto.ChatRoomRequestDto;
import com.complete.todayspace.domain.chat.dto.ChatRoomResponseDto;
import com.complete.todayspace.domain.chat.service.ChatService;
import com.complete.todayspace.global.dto.DataResponseDto;
import com.complete.todayspace.global.dto.StatusResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/chatroom")
    public ResponseEntity<StatusResponseDto> enterChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChatRoomRequestDto requestDto
    ) {

        chatService.enterChatRoom(userDetails.getUser().getId(), requestDto);

        StatusResponseDto responseDto = new StatusResponseDto(SuccessCode.ENTER_CHAT_ROOM);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/chatroom")
    public ResponseEntity<DataResponseDto<List<ChatRoomResponseDto>>> getChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        List<ChatRoomResponseDto> responseDto = chatService.getChatRoom(userDetails.getUser().getId());

        DataResponseDto<List<ChatRoomResponseDto>> dataResponseDto = new DataResponseDto<>(
                SuccessCode.CHATS_GET,
                responseDto
        );

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

    @MessageMapping("/chatroom/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessageRequestDto requestDto) {

        chatService.sendMessage(requestDto);

        roomMessageTemplate(roomId, requestDto.getMessage());
    }

    private void roomMessageTemplate(String roomId, String message) {
        simpMessagingTemplate.convertAndSend("/v1/sub/chatroom/" + roomId, message);
    }

}

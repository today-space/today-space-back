package com.complete.todayspace.domain.chat.controller;

import com.complete.todayspace.domain.chat.dto.ChatMessageRequestDto;
import com.complete.todayspace.domain.chat.dto.ChatMessageResponseDto;
import com.complete.todayspace.domain.chat.service.ChatMessageService;
import com.complete.todayspace.global.dto.DataResponseDto;
import com.complete.todayspace.global.entity.SuccessCode;
import com.complete.todayspace.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/chatroom/{roomId}/message")
    public ResponseEntity<DataResponseDto<List<ChatMessageResponseDto>>> getMessageForChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String roomId
    ) {

        List<ChatMessageResponseDto> responseDto = chatMessageService.getMessageForChatRoom(
                userDetails.getUser().getId(),
                roomId
        );

        DataResponseDto<List<ChatMessageResponseDto>> dataResponseDto = new DataResponseDto<>(
                SuccessCode.GET_MESSAGE_FOR_CHAT_ROOM,
                responseDto
        );

        return new ResponseEntity<>(dataResponseDto, HttpStatus.OK);
    }

    @MessageMapping("/chatroom/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessageRequestDto requestDto) {

        chatMessageService.sendMessage(requestDto);

        roomMessageTemplate(roomId, requestDto);
    }

    private void roomMessageTemplate(String roomId, ChatMessageRequestDto requestDto) {
        simpMessagingTemplate.convertAndSend("/v1/sub/chatroom/" + roomId, requestDto);
    }

}

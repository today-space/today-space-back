package com.complete.todayspace.domain.chat.service;

import com.complete.todayspace.domain.chat.dto.ChatMessageRequestDto;
import com.complete.todayspace.domain.chat.dto.ChatMessageResponseDto;
import com.complete.todayspace.domain.chat.entity.ChatMessage;
import com.complete.todayspace.domain.chat.repository.ChatMessageRepository;
import com.complete.todayspace.domain.chat.repository.ChatRoomRepository;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessageForChatRoom(Long id, String roomId) {

        if (!chatRoomRepository.existsByRoomIdAndUserId(roomId, id)) {
            throw new CustomException(ErrorCode.NO_CHAT_ROOM_OR_PERMISSION_DENIED);
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderBySendDateAsc(roomId);

        return chatMessages.stream()
                .map( (chatMessage) -> new ChatMessageResponseDto(
                        chatMessage.getSender(),
                        chatMessage.getMessage(),
                        chatMessage.getSendDate()
                ))
                .toList();
    }

    @Transactional
    public void sendMessage(ChatMessageRequestDto requestDto) {

        ChatMessage chatMessage = new ChatMessage(
                requestDto.getRoomId(),
                requestDto.getSender(),
                requestDto.getMessage()
        );
        chatMessageRepository.save(chatMessage);

    }

}

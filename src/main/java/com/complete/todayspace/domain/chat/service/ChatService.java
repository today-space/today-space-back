package com.complete.todayspace.domain.chat.service;

import com.complete.todayspace.domain.chat.dto.ChatRoomRequestDto;
import com.complete.todayspace.domain.chat.entity.ChatRoom;
import com.complete.todayspace.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void createChatRoom(ChatRoomRequestDto requestDto) {

        if (!chatRoomRepository.existsByRoomId(requestDto.getRoomId())) {

            ChatRoom chatRoom = new ChatRoom(requestDto);
            chatRoomRepository.save(chatRoom);

        }

    }

}

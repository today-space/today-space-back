package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.dto.ChatRoomResponseDto;

import java.util.List;

public interface ChatRoomRepositoryQuery {

    boolean existsByRoomIdAndUserId(String roomId, Long userId);

    List<ChatRoomResponseDto> findByChatRoomWithUserInfo(Long id);

}

package com.complete.todayspace.domain.chat.repository;

public interface ChatRoomRepositoryQuery {

    boolean existsByRoomIdAndUserId(String roomId, Long userId);

}

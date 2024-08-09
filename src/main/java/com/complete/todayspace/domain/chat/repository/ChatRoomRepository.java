package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByRoomId(String roomId);

}

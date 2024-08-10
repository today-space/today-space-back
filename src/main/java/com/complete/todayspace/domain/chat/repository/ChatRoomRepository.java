package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByRoomId(String roomId);

    List<ChatRoom> findBySellerOrBuyer(Long sellerId, Long buyerId);

}

package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.entity.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryQuery {

    boolean existsByRoomId(String roomId);

    List<ChatRoom> findBySellerOrBuyer(Long sellerId, Long buyerId);

}

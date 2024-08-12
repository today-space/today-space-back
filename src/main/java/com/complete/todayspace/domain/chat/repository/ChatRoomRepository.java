package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByRoomId(String roomId);

    List<ChatRoom> findBySellerOrBuyer(Long sellerId, Long buyerId);

    @Query("SELECT COUNT(c.id) > 0 " +
            "FROM ChatRoom c " +
            "WHERE c.roomId = :roomId " +
            "AND (c.seller = :userId OR c.buyer = :userId)")
    boolean existsByRoomIdAndUserId(@Param("roomId") String roomId, @Param("userId") Long userId);

}

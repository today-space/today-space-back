package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.entity.QChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryQueryImpl implements ChatRoomRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByRoomIdAndUserId(String roomId, Long userId) {
        int count = jpaQueryFactory.selectFrom(QChatRoom.chatRoom)
                .where(QChatRoom.chatRoom.roomId.eq(roomId)
                        .and(QChatRoom.chatRoom.seller.eq(userId)
                                .or(QChatRoom.chatRoom.buyer.eq(userId))))
                .fetch()
                .size();

        return count > 0;
    }

}

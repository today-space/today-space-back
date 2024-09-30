package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.dto.ChatRoomResponseDto;
import com.complete.todayspace.domain.chat.dto.QChatRoomResponseDto;
import com.complete.todayspace.domain.chat.entity.QChatRoom;
import com.complete.todayspace.domain.user.entity.QUser;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryQueryImpl implements ChatRoomRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByRoomIdAndUserId(String roomId, Long userId) {

        Integer count = jpaQueryFactory
                .selectOne()
                .from(QChatRoom.chatRoom)
                .where(QChatRoom.chatRoom.roomId.eq(roomId)
                        .and(QChatRoom.chatRoom.seller.eq(userId)
                                .or(QChatRoom.chatRoom.buyer.eq(userId))))
                .fetchFirst();

        return count != null;
    }

    @Override
    public List<ChatRoomResponseDto> findByChatRoomWithUserInfo(Long id) {

        QChatRoom chatRoom = QChatRoom.chatRoom;
        QUser buyer = QUser.user;
        QUser seller = new QUser("seller");

        return jpaQueryFactory.select(new QChatRoomResponseDto(
                        chatRoom.roomId,
                        Expressions.stringTemplate("CASE WHEN {0} = {1} THEN {2} ELSE {3} END",
                                chatRoom.seller, id, buyer.username, seller.username),
                        Expressions.stringTemplate("CASE WHEN {0} = {1} THEN {2} ELSE {3} END",
                                chatRoom.seller, id, buyer.profileImage, seller.profileImage)
                ))
                .from(chatRoom)
                .leftJoin(buyer).on(chatRoom.buyer.eq(buyer.id))
                .leftJoin(seller).on(chatRoom.seller.eq(seller.id))
                .where(chatRoom.seller.eq(id)
                        .or(chatRoom.buyer.eq(id)))
                .fetch();
    }

}

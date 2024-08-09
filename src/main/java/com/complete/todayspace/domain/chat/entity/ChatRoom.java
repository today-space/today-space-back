package com.complete.todayspace.domain.chat.entity;

import com.complete.todayspace.domain.chat.dto.ChatRoomRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_chat_room")
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String roomId;

    @Column
    private Long seller;

    @Column
    private Long buyer;

    public ChatRoom(ChatRoomRequestDto requestDto) {
        this.roomId = requestDto.getRoomId();
        this.seller = requestDto.getSeller();
        this.buyer = requestDto.getBuyer();
    }

}

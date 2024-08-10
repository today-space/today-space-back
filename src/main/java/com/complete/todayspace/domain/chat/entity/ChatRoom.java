package com.complete.todayspace.domain.chat.entity;

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

    public ChatRoom(String roomId, Long seller, Long buyer) {
        this.roomId = roomId;
        this.seller = seller;
        this.buyer = buyer;
    }

}

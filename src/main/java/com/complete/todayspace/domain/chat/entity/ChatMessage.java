package com.complete.todayspace.domain.chat.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "table_chat")
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    private String id;

    @Field
    private String roomId;

    @Field
    private String sender;

    @Field
    private String message;

    @Field
    @CreatedDate
    private LocalDateTime sendDate;

    public ChatMessage(String roomId, String sender, String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }

}

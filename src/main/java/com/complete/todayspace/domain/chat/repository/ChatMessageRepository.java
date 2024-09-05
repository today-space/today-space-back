package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByRoomIdOrderBySendDateAsc(String roomId);

}

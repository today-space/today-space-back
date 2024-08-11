package com.complete.todayspace.domain.chat.repository;

import com.complete.todayspace.domain.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<ChatMessage, String> {
}

package org.example.maeum2_be.repository;

import org.example.maeum2_be.entity.domain.Chat;
import org.example.maeum2_be.entity.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findChatsByChatRoom(ChatRoom chatRoom);
}

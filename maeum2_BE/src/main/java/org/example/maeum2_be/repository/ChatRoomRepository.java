package org.example.maeum2_be.repository;

import org.example.maeum2_be.entity.domain.ChatRoom;
import org.example.maeum2_be.entity.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Page<ChatRoom> findChatRoomsByMember(Member member, Pageable pageable);
    ChatRoom findChatRoomsById(Long id);
}

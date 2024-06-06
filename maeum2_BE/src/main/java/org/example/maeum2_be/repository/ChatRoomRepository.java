package org.example.maeum2_be.repository;

import org.example.maeum2_be.entity.domain.ChatRoom;
import org.example.maeum2_be.entity.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Page<ChatRoom> findChatRoomsByMember(Member member, Pageable pageable);
    ChatRoom findChatRoomsById(Long id);

    long countChatRoomByMember(Member member);
    @Query("SELECT COUNT(c) FROM ChatRoom c WHERE c.member = :member AND c.isSolved = true")
    long countByMemberAndIsSolved(@Param("member") Member member);
    @Query("SELECT COUNT(c) FROM ChatRoom c WHERE c.member = :member")
    long countByMember(@Param("member") Member member);

}

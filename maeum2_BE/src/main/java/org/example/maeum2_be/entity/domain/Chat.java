package org.example.maeum2_be.entity.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.repository.ChatRepository;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
@Getter
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id",nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private String message;



    public Chat(ChatRoom chatRoom,String message){
        this.chatRoom =chatRoom;
        this.message = message;
    }
    // Getters and Setters
}

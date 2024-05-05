package org.example.maeum2_be.entity.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

@Entity
@RequiredArgsConstructor
@Getter
public class ChatRoom {
    @Id
    @Column(name = "chat_room_id")
    private Long id;
}

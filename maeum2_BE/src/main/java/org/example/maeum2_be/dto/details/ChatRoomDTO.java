package org.example.maeum2_be.dto.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
public class ChatRoomDTO {
    Long id;
    LocalDateTime localDateTime;
}

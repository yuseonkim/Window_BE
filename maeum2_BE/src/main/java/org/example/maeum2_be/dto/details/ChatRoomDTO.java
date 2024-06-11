package org.example.maeum2_be.dto.details;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Getter
@AllArgsConstructor
public class ChatRoomDTO {
    Long id;

    @JsonProperty("isSolved")
    int isSolved;

    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate date;

    @JsonProperty("time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    LocalTime time;

    public ChatRoomDTO(Long id, int isSolved, LocalDateTime localDateTime) {
        this.id = id;
        this.isSolved = isSolved;
        this.date = localDateTime.toLocalDate();
        this.time = localDateTime.toLocalTime();
    }
}

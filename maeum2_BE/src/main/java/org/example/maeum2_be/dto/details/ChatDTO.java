package org.example.maeum2_be.dto.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;


@Data
@Getter
@AllArgsConstructor
public class ChatDTO {
    private int id;
    private String message;
}

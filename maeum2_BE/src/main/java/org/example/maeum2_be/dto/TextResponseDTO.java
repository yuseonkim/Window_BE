package org.example.maeum2_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class TextResponseDTO {
    private String message;
    private String status;
    private String phase;
    private boolean isSolved;
}
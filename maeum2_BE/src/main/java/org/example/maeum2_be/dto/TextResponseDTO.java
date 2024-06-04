package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class TextResponseDTO {
    private String message;
    private String status;
    private String chance;
    @JsonProperty("is_solved")
    private boolean isSolved;
    @JsonProperty("is_end")
    private boolean isEnd;
}
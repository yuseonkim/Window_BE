package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Text3ResponseDTO {
    private String message;
    private String status;
    private String number;
    @JsonProperty("is_end")
    private boolean isEnd;
    private String endBy;
}
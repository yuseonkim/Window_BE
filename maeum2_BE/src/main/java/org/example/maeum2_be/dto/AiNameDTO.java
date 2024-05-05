package org.example.maeum2_be.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AiNameDTO {
    @JsonProperty("ai_name")
    private String aiName;
}

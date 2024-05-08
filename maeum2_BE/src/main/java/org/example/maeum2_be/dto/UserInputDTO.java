package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserInputDTO {
    @JsonProperty("user_input")
    private String userInput;
}

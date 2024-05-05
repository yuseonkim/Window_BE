package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HomeDTO {
    @JsonProperty("child_first_name")
    String childFirstName;
    String message;
}

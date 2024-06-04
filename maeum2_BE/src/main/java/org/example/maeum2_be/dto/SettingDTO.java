package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class SettingDTO {
    @JsonProperty("child_first_name")
    String childFirstName;

    @JsonProperty("email")
    String email;
}

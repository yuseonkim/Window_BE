package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
    @JsonProperty("is_user")
    Boolean IsUser;
    @JsonProperty("member_id")
    String id;
}

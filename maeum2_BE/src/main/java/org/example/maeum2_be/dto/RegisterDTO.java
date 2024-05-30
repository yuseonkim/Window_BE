package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterDTO {
    @JsonProperty("member_id")
    String memberId;
}

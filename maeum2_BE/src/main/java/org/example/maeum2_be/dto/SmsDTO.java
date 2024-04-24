package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SmsDTO {
    @JsonProperty("phone_number")
    String phoneNumber;
}

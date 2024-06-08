package org.example.maeum2_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MemberUpdateDTO {
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String email;
    @JsonProperty("child_last_name")
    private String childLastName;

    @JsonProperty("child_first_name")
    private String childFirstName;

    @JsonProperty("child_birth")
    private LocalDate childBirth;

    @JsonProperty("child_gender")
    private String childGender;

    @JsonProperty("ai_name")
    private String aiName;
}

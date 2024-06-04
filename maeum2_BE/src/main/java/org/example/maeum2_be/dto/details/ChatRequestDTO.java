package org.example.maeum2_be.dto.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ChatRequestDTO {
    @JsonProperty("detail_id")
    Long detailId;
}

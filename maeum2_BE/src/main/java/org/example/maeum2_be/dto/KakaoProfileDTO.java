package org.example.maeum2_be.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoProfileDTO {

    private String id;

    @JsonProperty("properties")
    private Properties properties;

    @Data
    public static class Properties {
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;
    }
}

// toString, equals

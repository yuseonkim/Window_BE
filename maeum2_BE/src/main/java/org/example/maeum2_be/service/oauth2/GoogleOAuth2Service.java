package org.example.maeum2_be.service.oauth2;

import org.example.maeum2_be.dto.KakaoProfileDTO;
import org.springframework.stereotype.Service;

@Service

public class GoogleOAuth2Service implements OAuth2Interface {
    @Override
    public String getAccessToken(String code) {
        return null;
    }

    @Override
    public KakaoProfileDTO getUserInfo(String accessToken) {
        return null;
    }
}

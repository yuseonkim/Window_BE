package org.example.maeum2_be.service.oauth2;

import org.example.maeum2_be.dto.KakaoProfileDTO;
import org.springframework.stereotype.Service;

@Service
public interface OAuth2Service {
    String getAccessToken(String code);
    KakaoProfileDTO getUserInfo(String accessToken);
}

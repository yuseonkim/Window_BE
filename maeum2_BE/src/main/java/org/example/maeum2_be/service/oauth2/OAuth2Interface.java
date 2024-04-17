package org.example.maeum2_be.service.oauth2;

import org.example.maeum2_be.dto.KakaoProfileDTO;
import org.springframework.stereotype.Service;

public interface OAuth2Interface {
    String getAccessToken(String code);
    KakaoProfileDTO getUserInfo(String accessToken);
}

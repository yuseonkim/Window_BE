package org.example.maeum2_be.service.oauth2;


import org.example.maeum2_be.dto.KakaoProfileDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoOAuth2Service implements OAuth2Interface {
    @Value("${oauth2.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;
    @Value("${oauth2.kakao.client-id}")
    private String KAKAO_CLIENT_ID;
    @Value("${oauth2.kakao.token-uri}")
    private String KAKAO_TOKEN_URI;
    @Value("${oauth2.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URI;

    @Override
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                KAKAO_TOKEN_URI,
                kakaoTokenRequest,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    @Override
    public KakaoProfileDTO getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<KakaoProfileDTO> response = restTemplate.postForEntity(
                KAKAO_USER_INFO_URI,
                kakaoProfileRequest,
                KakaoProfileDTO.class
        );

        return response.getBody();
    }

}

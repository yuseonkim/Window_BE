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


public class GoogleOAuth2Service implements OAuth2Interface {

    @Value("${oauth2.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;
    @Value("${oauth2.google.client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${oauth2.google.token-uri}")
    private String GOOGLE_TOKEN_URI;
    @Value("${oauth2.google.user-info-uri}")
    private String GOOGLE_USER_INFO_URI;
    @Value("${oauth2.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    /*
POST /token HTTP/1.1
Host: oauth2.googleapis.com
Content-Type: application/x-www-form-urlencoded

code=4/P7q7W91a-oMsCeLvIaQm6bTrgtp7&
client_id=your_client_id&
client_secret=your_client_secret&
redirect_uri=https%3A//oauth2.example.com/code&
grant_type=authorization_code
 */
    @Override
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", GOOGLE_CLIENT_ID);
        params.add("redirect_uri", GOOGLE_REDIRECT_URI);
        params.add("client_secret",GOOGLE_CLIENT_SECRET);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                GOOGLE_TOKEN_URI,
                googleTokenRequest,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    @Override
    public KakaoProfileDTO getUserInfo(String accessToken) {
        return null;
    }
}

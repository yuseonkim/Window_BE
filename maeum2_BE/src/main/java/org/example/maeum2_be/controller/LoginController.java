package org.example.maeum2_be.controller;



import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be.dto.OAuthDTO;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.service.oauth2.GoogleOAuth2Service;
import org.example.maeum2_be.service.oauth2.KakaoOAuth2Service;
import org.example.maeum2_be.service.oauth2.LoginFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginFactory loginFactory;
    private final GoogleOAuth2Service googleOAuth2Service;

    @PostMapping("/api/login/kakao")
    public ApiResponse<?> kakaoLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody OAuthDTO oAuthDTO){
        System.out.println(oAuthDTO.getCode());
        return loginFactory.kakaoLogin(request,response, oAuthDTO.getCode());
    }

    @PostMapping("/api/login/google")
    public String googleLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody OAuthDTO oAuthDTO){
        return googleOAuth2Service.getAccessToken(oAuthDTO.getCode());
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal PrincipalDetails principalDetails){
        return principalDetails.getMember().toString();
    }
}

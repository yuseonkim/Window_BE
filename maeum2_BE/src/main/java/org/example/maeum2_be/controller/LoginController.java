package org.example.maeum2_be.controller;



import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be.dto.OAuthDTO;
import org.example.maeum2_be.service.oauth2.KakaoOAuth2Service;
import org.example.maeum2_be.service.oauth2.LoginFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginFactory loginFactory;

    @PostMapping("/api/login/kakao")
    public ApiResponse<?> kakaoLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody OAuthDTO oAuthDTO){
        System.out.println(oAuthDTO.getCode());
        return loginFactory.kakaoLogin(request,response, oAuthDTO.getCode());
    }
}

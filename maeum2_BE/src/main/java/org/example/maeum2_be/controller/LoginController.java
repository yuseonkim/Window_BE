package org.example.maeum2_be.controller;



import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.service.oauth2.KakaoOAuth2Service;
import org.example.maeum2_be.service.oauth2.LoginFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginFactory loginFactory;

    @PostMapping("/api/login/kakao")
    public ResponseEntity<?> kakaoLogin(HttpServletRequest request, HttpServletResponse response, String code){
        System.out.println(code);
        return loginFactory.kakaoLogin(request,response,code);
    }
}

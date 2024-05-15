package org.example.maeum2_be.controller;


import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.AiNameDTO;
import org.example.maeum2_be.dto.MemberDTO;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.exception.AccessDeniedException;
import org.example.maeum2_be.service.register.AiNameSetter;
import org.example.maeum2_be.service.register.UserRegister;
import org.example.maeum2_be.utils.jwt.JwtTokenCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final UserRegister userRegister;
    private final JwtTokenCreator jwtTokenCreator;
    private final AiNameSetter aiNameSetter;

    @PostMapping("/api/user/signUp")
    public ApiResponse<?> signUp(HttpServletResponse response,
                                 @RequestBody MemberDTO memberDTO,
                                 @AuthenticationPrincipal PrincipalDetails principalDetails){
       Member member =  userRegister.signUpUser(principalDetails,memberDTO);
       jwtTokenCreator.execute(member);

        String jwt = jwtTokenCreator.execute(member);

        response.setContentType("application/json");
        //새로 발급된 토큰을 HTTP 헤더에 추가
        response.addHeader("Authorization", jwt);
        return ApiResponseGenerator.success(HttpStatus.OK);
    }

    @PostMapping("/api/user/aiName")
    public ApiResponse<?> setAiName(@AuthenticationPrincipal PrincipalDetails principalDetails, AiNameDTO aiNameDTO){
        String memberId = principalDetails.getMemberId();
        String aiName = aiNameDTO.getAiName();
        return aiNameSetter.execute(memberId,aiName);
    }
}

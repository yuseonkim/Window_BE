package org.example.maeum2_be.service.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.KakaoProfileDTO;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.repository.MemberRepository;
import org.example.maeum2_be.utils.jwt.JwtTokenCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginFactory {

    private final KakaoOAuth2Service kakaoOauth2;
    private final GoogleOAuth2Service googleOAuth2Service;
    private final MemberRepository memberRepository;
    private final JwtTokenCreator jwtTokenCreator;

    public ApiResponse<?> kakaoLogin(HttpServletRequest request, HttpServletResponse response, String code){
        String accessToken = kakaoOauth2.getAccessToken(code);
        KakaoProfileDTO kakaoProfileDTO = kakaoOauth2.getUserInfo(accessToken);
        Member member = memberRepository.findByMemberId(kakaoProfileDTO.getId());

        if(member == null){
            Member newMember = Member.builder().memberId(kakaoProfileDTO.getId()).build();
            memberRepository.saveAndFlush(newMember);
        }

        String jwt = jwtTokenCreator.execute(member);

        PrincipalDetails principalDetails = new PrincipalDetails(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null,
                principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        response.setContentType("application/json");
        // 토큰을 HTTP 헤더에 추가
        response.addHeader("Authorization", jwt);

        return ApiResponseGenerator.success(HttpStatus.OK);
    }




}

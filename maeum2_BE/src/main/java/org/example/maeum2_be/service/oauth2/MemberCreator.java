package org.example.maeum2_be.service.oauth2;


import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.dto.KakaoProfileDTO;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.Role;
import org.example.maeum2_be.repository.MemberRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class MemberCreator {
    private final KakaoOAuth2Service kakaoOauth2;
    private final GoogleOAuth2Service googleOAuth2Service;
    private final MemberRepository memberRepository;

    public Member execute(String code){
        String accessToken = kakaoOauth2.getAccessToken(code);
        KakaoProfileDTO kakaoProfileDTO = kakaoOauth2.getUserInfo(accessToken);
        Member member = memberRepository.findByMemberId(kakaoProfileDTO.getId());

        if(member == null){
            Member newMember = Member.builder().memberId(kakaoProfileDTO.getId()).build();
            newMember.changeUserRole(Role.ROLE_BEGINNER);
            memberRepository.saveAndFlush(newMember);
            return newMember;
        }

        return member;
    }




}

package org.example.maeum2_be.service.register;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.dto.MemberDTO;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.repository.MemberRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegister {

    private final MemberRepository memberRepository;
    public void signUpUser(@AuthenticationPrincipal PrincipalDetails principalDetails, MemberDTO memberDTO){
        String memberId = principalDetails.getMemberId();
        Member member =  memberRepository.findByMemberId(memberId);
        member.setUserInfo(memberDTO.getPhoneNumber(), memberDTO.getEmail(),memberDTO.getChildLastName(),memberDTO.getChildFirstName(),memberDTO.getChildBirth(),memberDTO.getChildGender());
        memberRepository.saveAndFlush(member);
    }
}

package org.example.maeum2_be.service.register;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be._core.MessageCode;
import org.example.maeum2_be.dto.MemberDTO;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.entity.domain.Role;
import org.example.maeum2_be.exception.MemberNotFoundException;
import org.example.maeum2_be.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegister {

    private final MemberRepository memberRepository;
    public Member signUpUser(@AuthenticationPrincipal PrincipalDetails principalDetails, MemberDTO memberDTO){
        String memberId = principalDetails.getMemberId();
        Member member =  memberRepository.findByMemberId(memberId);
        if(member == null){
            throw new MemberNotFoundException(MessageCode.MEMBER_NOT_FOUND);
        }
        member.setUserInfo(memberDTO.getPhoneNumber(), memberDTO.getEmail(),memberDTO.getChildLastName(),memberDTO.getChildFirstName(),memberDTO.getChildBirth(),memberDTO.getChildGender());
        member.changeUserRole(Role.ROLE_USER);
        memberRepository.saveAndFlush(member);
        return member;
    }

    public ApiResponse<?> changeUserInfo(String memberId, MemberDTO memberDTO){
        Member member = memberRepository.findByMemberId(memberId);
        if(member == null){
            throw new MemberNotFoundException(MessageCode.MEMBER_NOT_FOUND);
        }
        member.setUserInfo(memberDTO.getPhoneNumber(), memberDTO.getEmail(),memberDTO.getChildLastName(),memberDTO.getChildFirstName(),memberDTO.getChildBirth(),memberDTO.getChildGender());
        memberRepository.saveAndFlush(member);
        return ApiResponseGenerator.success(HttpStatus.OK);
    }
}

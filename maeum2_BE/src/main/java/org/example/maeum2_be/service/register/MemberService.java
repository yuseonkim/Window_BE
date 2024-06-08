package org.example.maeum2_be.service.register;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.dto.MemberUpdateDTO;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private  final MemberRepository memberRepository;

    @Transactional
    public Member updateMemberInfo(String memberId, MemberUpdateDTO memberUpdateDTO) {
        Member member = memberRepository.findByMemberId(memberId);

        if (isNotNullOrEmpty(memberUpdateDTO.getPhoneNumber())) {
            member.setPhoneNumber(memberUpdateDTO.getPhoneNumber());
        }
        if (isNotNullOrEmpty(memberUpdateDTO.getEmail())) {
            member.setEmail(memberUpdateDTO.getEmail());
        }
        if (isNotNullOrEmpty(memberUpdateDTO.getChildLastName())) {
            member.setChildLastName(memberUpdateDTO.getChildLastName());
        }
        if (isNotNullOrEmpty(memberUpdateDTO.getChildFirstName())) {
            member.setChildFirstName(memberUpdateDTO.getChildFirstName());
        }
        if (memberUpdateDTO.getChildBirth() != null) {
            member.setChildBirth(memberUpdateDTO.getChildBirth());
        }
        if (isNotNullOrEmpty(memberUpdateDTO.getChildGender())) {
            member.setChildGender(memberUpdateDTO.getChildGender());
        }
        if(isNotNullOrEmpty(memberUpdateDTO.getAiName())){
            member.setAiName(memberUpdateDTO.getAiName());
        }
        memberRepository.save(member);
        return member;
    }

    private boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

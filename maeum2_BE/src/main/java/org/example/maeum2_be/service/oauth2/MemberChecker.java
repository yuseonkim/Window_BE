package org.example.maeum2_be.service.oauth2;


import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.Role;
import org.example.maeum2_be.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberChecker {

    private final MemberRepository memberRepository;
    public boolean isRegister(String id){
        Member member = memberRepository.findByMemberId(id);
        return member.getRole() != Role.NotUser;
    }

    public boolean isFirstLogin(String id){
        Member member = memberRepository.findByMemberId(id);
        if(member == null){
            return true;
        }
        return false;
    }
}

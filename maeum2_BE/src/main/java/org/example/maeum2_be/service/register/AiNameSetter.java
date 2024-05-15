package org.example.maeum2_be.service.register;


import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be._core.MessageCode;
import org.example.maeum2_be.dto.AiNameDTO;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.exception.AccessDeniedException;
import org.example.maeum2_be.exception.MemberNotFoundException;
import org.example.maeum2_be.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiNameSetter {
    private final MemberRepository memberRepository;

    public ApiResponse<?> execute(String memberId, String aiName){
        Member member = memberRepository.findByMemberId(memberId);
        if(aiName == null){
            throw new AccessDeniedException(MessageCode.REQUEST_ACCESS_DENIED);
        }
        if(member == null){
            throw new MemberNotFoundException(MessageCode.MEMBER_NOT_FOUND);
        }
        member.changeAiName(aiName);

        memberRepository.saveAndFlush(member);
        return ApiResponseGenerator.success(HttpStatus.OK);
    }
}

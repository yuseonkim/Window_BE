package org.example.maeum2_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.HomeDTO;
import org.example.maeum2_be.dto.myPage.GuardianInformation;
import org.example.maeum2_be.dto.myPage.KidInformation;
import org.example.maeum2_be.dto.myPage.MyPageDTO;
import org.example.maeum2_be.entity.domain.Member;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

    @GetMapping("/api/home")
    public ApiResponse<?> getHome(@AuthenticationPrincipal PrincipalDetails principalDetails){
        String childFristName = principalDetails.getUsername();
        String message = "오늘 하루도 힘내자!";
        return ApiResponseGenerator.success(new HomeDTO(childFristName,message),HttpStatus.OK);
    }

    @GetMapping("/api/test")
    public ApiResponse<?> getTest(){
        String message = "테스트 성공";
        return ApiResponseGenerator.success(message,HttpStatus.OK);
    }

    @GetMapping("/api/myPage")
    public ApiResponse<?> getMyPage(@AuthenticationPrincipal PrincipalDetails principalDetails){
        Member member = memberRepository.findByMemberId(principalDetails.getMemberId());
        List<KidInformation> kidInformations = new ArrayList<>();
        kidInformations.add(new KidInformation("캐릭터이름",member.getAiName()));
        kidInformations.add(new KidInformation("성",member.getChildLastName()));
        kidInformations.add(new KidInformation("이름",member.getChildFirstName()));
        kidInformations.add(new KidInformation("생년월일",member.getChildBirth().toString()));
        kidInformations.add(new KidInformation("성별",member.getChildGender()));

        List<GuardianInformation> guardianInformations = new ArrayList<>();
        guardianInformations.add(new GuardianInformation("이메일", member.getEmail()));
        guardianInformations.add(new GuardianInformation("연락처", member.getPhoneNumber()));

        MyPageDTO myPageDTO = new MyPageDTO(kidInformations,guardianInformations);
        return ApiResponseGenerator.success(myPageDTO, HttpStatus.OK);
    }


}

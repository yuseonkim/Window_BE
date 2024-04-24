package org.example.maeum2_be.controller;


import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be.dto.SmsDTO;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.service.register.SmsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/api/user/sms/send")
    public ApiResponse<?> sendVerificationCode(@RequestBody SmsDTO smsDTO){
        System.out.println(smsDTO.getPhoneNumber());
        return smsService.sendVerificationCode(smsDTO);
    }
}

package org.example.maeum2_be.service.register;

import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.SmsDTO;
import org.example.maeum2_be.entity.domain.Sms;
import org.example.maeum2_be.repository.SmsRepository;
import org.example.maeum2_be.utils.sms.SmsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class SmsService {
    private final SmsUtil smsUtil;
    private final SmsRepository smsRepository;
    public ApiResponse<?> sendVerificationCode(SmsDTO smsDTO) {
        //수신번호 형태에 맞춰 "-"을 ""로 변환
        String phoneNumber = smsDTO.getPhoneNumber().replaceAll("-","");
        // Random 객체 생성
        Random random = new Random();

        // 1000부터 9999까지의 난수 생성
        int randomNumber = random.nextInt(9000) + 1000;
        // 정수를 문자열로 변환
        String verificationCode = String.valueOf(randomNumber);
        smsUtil.sendOne(phoneNumber, verificationCode);
        Sms sms = new Sms(phoneNumber,verificationCode);
        smsRepository.save(sms);


        return ApiResponseGenerator.success(HttpStatus.OK);
    }
}

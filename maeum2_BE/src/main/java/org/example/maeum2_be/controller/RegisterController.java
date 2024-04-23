package org.example.maeum2_be.controller;


import lombok.RequiredArgsConstructor;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.MemberDTO;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.example.maeum2_be.service.register.UserRegister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final UserRegister userRegister;

    @PostMapping("/api/user/signUp")
    public ApiResponse<?> signUp(HttpServletResponse response,
                                 @RequestBody MemberDTO memberDTO,
                                 @AuthenticationPrincipal PrincipalDetails principalDetails){
        userRegister.signUpUser(principalDetails,memberDTO);

        return ApiResponseGenerator.success(HttpStatus.OK);
    }
}

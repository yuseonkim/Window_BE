package org.example.maeum2_be.controller;

import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be.dto.HomeDTO;
import org.example.maeum2_be.entity.domain.PrincipalDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/api/home")
    public ApiResponse<?> home(@AuthenticationPrincipal PrincipalDetails principalDetails){
        String childFirstName = principalDetails.getUsername();
        String message ="일단 테스트";
        return ApiResponseGenerator.success(new HomeDTO(childFirstName,message), HttpStatus.OK);
    }
}

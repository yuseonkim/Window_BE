package org.example.maeum2_be.utils.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.maeum2_be._core.ApiResponse;
import org.example.maeum2_be._core.ApiResponseGenerator;
import org.example.maeum2_be._core.MessageCode;
import org.springframework.http.HttpStatus;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FilterResponseUtils {

    public static void unAuthorized(HttpServletResponse response) throws IOException {
        ApiResponse<?> apiResponse = ApiResponseGenerator.fail(MessageCode.NEED_LOGIN.getCode(), MessageCode.NEED_LOGIN.getValue(), HttpStatus.UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse.getBody()));
    }


    public static void forbidden(HttpServletResponse response, Boolean isRoleNotUser) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        // 현재 사용자의 권한에 따라 다른 에러 메시지를 설정
        if (isRoleNotUser) {
            ApiResponse<?> apiResponse = ApiResponseGenerator.fail(MessageCode.NEED_REGISTER.getCode(), MessageCode.NEED_REGISTER.getValue(), HttpStatus.FORBIDDEN);
            response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse.getBody()));
//
        } else {
            unAuthorized(response);
        }
    }
    public static void AccessTokenExpired(HttpServletResponse response) throws IOException {
        ApiResponse<?> apiResponse = ApiResponseGenerator.fail(MessageCode.EXPIRED_ACCESS_TOKEN.getCode(), MessageCode.EXPIRED_ACCESS_TOKEN.getValue(), HttpStatus.UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse.getBody()));
    }

    public static void AccessTokenValidationException(HttpServletResponse response) throws  IOException{
        ApiResponse<?> apiResponse = ApiResponseGenerator.fail(MessageCode.INVALIDATE_ACCESS_TOKEN.getCode(), MessageCode.INVALIDATE_ACCESS_TOKEN.getValue(), HttpStatus.UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse.getBody()));
    }

    public static void RefreshTokenExpiredException(HttpServletResponse response) throws  IOException{
        ApiResponse<?> apiResponse = ApiResponseGenerator.fail("401", "리프레시 토큰 만료", HttpStatus.UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse.getBody()));
    }
}

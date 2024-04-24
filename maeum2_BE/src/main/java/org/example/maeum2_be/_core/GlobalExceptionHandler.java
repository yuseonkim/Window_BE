package org.example.maeum2_be._core;


import org.example.maeum2_be.exception.VerificationCodeNotEqualException;
import org.example.maeum2_be.exception.VerificationCodeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VerificationCodeNotEqualException.class)
    public ApiResponse<ApiResponse.CustomBody> handleMemberNotFoundException(
            VerificationCodeNotEqualException verificationCodeNotEqualException) {
        return ApiResponseGenerator.fail(verificationCodeNotEqualException.getMessageCode().getCode(),
                verificationCodeNotEqualException.getMessageCode().getValue(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ApiResponse<ApiResponse.CustomBody> verificationCodeNotFoundException(
            VerificationCodeNotFoundException verificationCodeNotFoundException) {
        return ApiResponseGenerator.fail(verificationCodeNotFoundException.getMessageCode().getCode(),
                verificationCodeNotFoundException.getMessageCode().getValue(), HttpStatus.BAD_REQUEST);
    }


}

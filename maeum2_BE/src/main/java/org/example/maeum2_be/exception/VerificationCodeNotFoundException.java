package org.example.maeum2_be.exception;

import lombok.Getter;
import org.example.maeum2_be._core.MessageCode;

@Getter
public class VerificationCodeNotFoundException extends RuntimeException{
    public final MessageCode messageCode;

    public VerificationCodeNotFoundException(MessageCode messageCode) {
        this.messageCode = messageCode;
    }


    public VerificationCodeNotFoundException(MessageCode messageCode, Exception e) {
        this.messageCode = messageCode;
    }
}

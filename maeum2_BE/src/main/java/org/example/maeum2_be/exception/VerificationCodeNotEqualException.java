package org.example.maeum2_be.exception;

import lombok.Getter;
import org.example.maeum2_be._core.MessageCode;

@Getter
public class VerificationCodeNotEqualException extends RuntimeException{
    public final MessageCode messageCode;

    public VerificationCodeNotEqualException(MessageCode messageCode) {
        this.messageCode = messageCode;
    }


    public VerificationCodeNotEqualException(MessageCode messageCode, Exception e) {
        this.messageCode = messageCode;
    }
}


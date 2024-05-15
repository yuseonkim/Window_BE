package org.example.maeum2_be.exception;

import lombok.Getter;
import org.example.maeum2_be._core.MessageCode;

@Getter
public class AccessDeniedException extends RuntimeException{
    public final MessageCode messageCode;

    public AccessDeniedException(MessageCode messageCode) {
        this.messageCode = messageCode;
    }

    public AccessDeniedException(MessageCode messageCode, Exception e) {
        this.messageCode = messageCode;
    }
}

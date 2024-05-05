package org.example.maeum2_be.exception;

import lombok.Getter;
import org.example.maeum2_be._core.MessageCode;

@Getter
public class MemberNotFoundException extends  RuntimeException{
    public final MessageCode messageCode;

    public MemberNotFoundException(MessageCode messageCode) {
        this.messageCode = messageCode;
    }

    public MemberNotFoundException(MessageCode messageCode, Exception e) {
        this.messageCode = messageCode;
    }
}

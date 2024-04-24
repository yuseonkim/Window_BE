package org.example.maeum2_be.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;


@AllArgsConstructor
@Getter
public class Sms {

    @Id
    private String phoneNumber;
    private String verificationCode;
}

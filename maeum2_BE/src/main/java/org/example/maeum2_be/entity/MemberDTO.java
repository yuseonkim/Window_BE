package org.example.maeum2_be.entity;

import lombok.Getter;

import java.util.Date;

@Getter
public class MemberDTO {
    Long MemberId;
    String childFirstName;
    String childLastName;
    String childGender;
    Date birth;
    String email;
    String phoneNumber;
}

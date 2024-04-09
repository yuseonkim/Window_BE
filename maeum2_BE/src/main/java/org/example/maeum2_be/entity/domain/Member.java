package org.example.maeum2_be.entity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
@Table
public class Member {
    @Id
    private String memberId;

    @Column(nullable = false, length = 20)
    private String childFirstName;

    @Column(nullable = false, length = 20)
    private String childLastName;


    @Column(nullable = false)
    private Gender childGender;

    @Column(nullable = false)
    private Date birth;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String aiName;

    @Column(nullable = false, columnDefinition = "NotUser")
    private Role role;



    @Builder
    public Member(String memberId, String childFirstName, String childLastName, Gender childGender, Date birth, String email, String phoneNumber){
        this.memberId = memberId;
        this.childFirstName = childFirstName;
        this.childLastName = childLastName;
        this.childGender = childGender;
        this.birth = birth;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}

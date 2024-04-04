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
    private Long memberId;

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

    @Builder





}

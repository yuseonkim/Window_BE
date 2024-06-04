package org.example.maeum2_be.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.example.maeum2_be.entity.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenCreator {
    @Value("86400")
    private Long accessEXP;

    @Value("${jwt.secret}")
    private String SECRET;

    public static String TOKEN_PREFIX = "Bearer ";


    public String execute(Member member) {
        String jwt = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400 * 1000))
                .withClaim("memberId", member.getMemberId())
                .withClaim("childFirstName", member.getChildFirstName())
                .withClaim("childLastName", member.getChildLastName())
                .withClaim("aiName", member.getAiName())
                .withClaim("email",member.getEmail())
                .withClaim("role", member.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET));
        return TOKEN_PREFIX + jwt;
    }
}

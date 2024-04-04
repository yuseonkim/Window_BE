package org.example.maeum2_be.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.example.maeum2_be.entity.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenCreator {

    private final Key key;
    private final long accessTokenExpiredTime;

    public JwtTokenCreator(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") Long expireTime
    ){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiredTime = expireTime;
    }

    public String createAccessToken(Member member){
        return createToken(accessTokenExpiredTime,member);
    }

    private String createToken(long accessTokenExpiredTime, Member member) {
        Claims claims = Jwts.claims();
        claims.put("memberId",member.getMemberId());
        claims.put("memberId",member.getChildFirstName());
        claims.put("memberId",member.getAiName());
        claims.put("role",member.getRole());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(accessTokenExpiredTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

}

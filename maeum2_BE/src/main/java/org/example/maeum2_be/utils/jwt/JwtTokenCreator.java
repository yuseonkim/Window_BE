package org.example.maeum2_be.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.example.maeum2_be.entity.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

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
        claims.put("memberId",customUserInfoDTO.getMemberId());
        claims.put("memberId",)
    }

}

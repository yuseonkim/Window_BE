package org.example.maeum2_be.repository;


import org.example.maeum2_be.entity.domain.Sms;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class SmsRepository {
    private final RedisTemplate<String,String> redisTemplate;

    public SmsRepository(final RedisTemplate<String,String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(final Sms sms) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(sms.getPhoneNumber(), sms.getVerificationCode());
        redisTemplate.expire(sms.getVerificationCode(), 60L, TimeUnit.SECONDS);
    }

    public Optional<String> findVerificationCode(String phoneNumber){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return Optional.ofNullable(valueOperations.get(phoneNumber));
    }

    public String deleteVerificationCode(String phoneNumber){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.getAndDelete(phoneNumber);
    }


}

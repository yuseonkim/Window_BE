package org.example.maeum2_be.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConversationRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(final String key, final String text) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(key, text);
    }

    public List<String> getConversations(final String key) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        return listOps.range(key, 0, -1);
    }
}

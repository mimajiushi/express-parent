package com.cwj.express.ucenter.service.impl;

import com.cwj.express.ucenter.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis操作Service的实现类
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setList(String key, List list){
        redisTemplate.opsForList().rightPushAll(key, list);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean setKeyValTTL(String key, String value, long ttl) {
        stringRedisTemplate.boundValueOps(key).set(value,ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire>0;
    }

    @Override
    public boolean expire(String key, long expire) {
        return stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public Boolean zadd(String key, String menber, double score) {
        return stringRedisTemplate.opsForZSet().add(key, menber, score);
    }

    @Override
    public Double increment(String key, String menber, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, menber, delta);
    }

    @Override
    public Double zscore(String key, String menber) {
        return stringRedisTemplate.opsForZSet().score(key, menber);
    }

    @Override
    public Long zrem(String key, Object... menbers) {
        return stringRedisTemplate.opsForZSet().remove(key, menbers);
    }
}

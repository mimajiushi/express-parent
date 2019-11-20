package com.cwj.express.order.service.impl;

import com.cwj.express.order.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis操作Service的实现类
 * Created by macro on 2018/8/7.
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
    public void zadd(String key, String menber, double score) {
        stringRedisTemplate.opsForZSet().add(key, menber, score);
    }

    @Override
    public double zincrby(String key, String menber, double score) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, menber, score);
    }

    @Override
    public void zrem(String key, Object... menbers) {
        stringRedisTemplate.opsForZSet().remove(key, menbers);
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
    public Double increment(String key, String menber, double delta) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, menber, delta);
    }
}

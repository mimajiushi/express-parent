package com.cwj.express.area;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author chenwenjie
 */
@SpringBootApplication
@MapperScan(basePackages = "com.cwj.express.area.dao")
@ComponentScan(basePackages = "com.cwj.express.common")
@ComponentScan(basePackages = "com.cwj.express.api")
@ComponentScan(basePackages = "com.cwj.express.utils")
@Slf4j
public class ExpressAreaApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExpressAreaApplication.class, args);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
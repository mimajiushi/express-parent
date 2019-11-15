package com.cwj.express.order.config.lua;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * lua脚本配置类
 * todo 还没进行单元测试
 */

@Configuration
public class LuaConfiguration {
    @Bean
    public DefaultRedisScript<String> redisScript() {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/distribution")));
        redisScript.setResultType(String.class);
        return redisScript;
    }
}
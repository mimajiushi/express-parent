package com.cwj.express.common.config.ribbon;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xwj
 * 全局ribbon负载均衡配置
 */
@Configuration
public class RibbonRuleConfig {
    @Bean
    public IRule ribbonRule() {
        return new ZoneAvoidanceRule();
    }
}

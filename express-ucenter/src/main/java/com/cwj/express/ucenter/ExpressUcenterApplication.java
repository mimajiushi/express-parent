package com.cwj.express.ucenter;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

/**
 * @author chenwenjie
 */
@SpringBootApplication
@MapperScan(basePackages = "com.cwj.express.ucenter.dao")
@ComponentScan(basePackages = "com.cwj.express.common")
@ComponentScan(basePackages = "com.cwj.express.api")
@ComponentScan(basePackages = "com.cwj.express.utils")
@Slf4j
@EnableFeignClients
@EnableCaching
public class ExpressUcenterApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExpressUcenterApplication.class, args);
    }

    /**
     * 引入 security 之后默认实现是 StrictHttpFirewall 会拦截带;号的请求(第一次请求静态资源url是http://xxx;jsessionid=xxx)
     * 这里覆盖 security 原来的实现解决。当然实际生产环境静态资源应由nginx代理，由nginx代理就不会产生这种问题了
     */
    @Bean
    public HttpFirewall httpFirewall() {
        return new DefaultHttpFirewall();
    }


}
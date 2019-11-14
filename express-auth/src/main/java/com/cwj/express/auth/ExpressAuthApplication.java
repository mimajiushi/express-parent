package com.cwj.express.auth;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenwenjie
 */
@SpringBootApplication
@MapperScan(basePackages = "com.cwj.express.auth.dao")
@ComponentScan(basePackages = "com.cwj.express.common")
@ComponentScan(basePackages = "com.cwj.express.api")
@ComponentScan(basePackages = "com.cwj.express.utils")
@Slf4j
public class ExpressAuthApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExpressAuthApplication.class, args);
    }

    @Bean
    @LoadBalanced //ribbon负载均衡 https://blog.csdn.net/u011063112/article/details/81295376
    @SentinelRestTemplate //服务熔断
    public RestTemplate restTemplate() {
//        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }


}
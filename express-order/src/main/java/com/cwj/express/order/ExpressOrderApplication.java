package com.cwj.express.order;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.cwj.express.order.dao")
@ComponentScan(basePackages = "com.cwj.express.common")
@ComponentScan(basePackages = "com.cwj.express.api")
@ComponentScan(basePackages = "com.cwj.express.utils")
@Slf4j
@EnableFeignClients
@EnableCaching
public class ExpressOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpressOrderApplication.class, args);
    }
}

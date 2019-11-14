package com.cwj.express.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author swj
 * 必须配置静态资源，security 放行 /public/** 才有效
 * 本项目因为集成了spring boot swagger，不设置成static是防止冲突
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 配置静态资源
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                //配置swagger，它是在这个目录下的
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/public/**")
                .addResourceLocations("classpath:/public/");
        super.addResourceHandlers(registry);
    }
}

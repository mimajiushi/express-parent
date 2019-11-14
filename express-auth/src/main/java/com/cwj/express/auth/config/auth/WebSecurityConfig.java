package com.cwj.express.auth.config.auth;

import com.cwj.express.auth.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@Order(-1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

    /**
     * 采用bcrypt对密码进行编码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] ignoreUrl = {
                "/auth/userLogin", "/auth/userLogout", "/auth/loginSendSms",
                "/page/**", "/public/**",
                // swagger
                "/",
                "/csrf",
                "/docs.html",
                "/v2/**",
                "/webjars/**",
                "/swagger-ui.html",
                "/static/**",
                "/swagger-resources/**"
        };
        MyAuthenticationProvider myAuthenticationProvider = new MyAuthenticationProvider();
        myAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        myAuthenticationProvider.setUserDetailsService(userDetailsService);

        // 禁用缓存
        http.headers().cacheControl();
        // 关闭csrf跨域
        http.csrf().disable()
                // 允许http basic 认证
                .httpBasic().and()
                // 允许表单认证
                .formLogin()
                .loginPage("/page/index").and()
                .authenticationProvider(myAuthenticationProvider)
                .authorizeRequests()

                .antMatchers(ignoreUrl).permitAll()

                .anyRequest().authenticated();

    }
}

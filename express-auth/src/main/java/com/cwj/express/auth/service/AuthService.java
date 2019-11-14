package com.cwj.express.auth.service;

/**
 * @author root
 */
public interface AuthService {
    /**
     * 登录接口
     * @return token
     */
     String login(String username, String password, String clientId, String clientSecret);
}

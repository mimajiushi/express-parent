package com.cwj.express.auth.config.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chenwenjie
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
    private Integer tokenValiditySeconds;

    private String clientId;

    private String clientSecret;

    private String cookieDomain;

    private Integer cookieMaxAge;
}

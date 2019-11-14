package com.cwj.express.auth.config.thirdapi;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chenwenjie
 */
@Data
@Component("thirdApiConfig")
@ConfigurationProperties(prefix = "third-api-config")
public class ThirdApiConfig {
    private SmsConfig sms;
    private BaiduFaceConfig baidu;
}

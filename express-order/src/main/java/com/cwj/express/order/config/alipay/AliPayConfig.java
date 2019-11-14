package com.cwj.express.order.config.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author cwj
 */

@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AliPayConfig {
    private String uid;
    private String appId;
    private String signType;
    private String gatewayUrl;
    private String merchantPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;

    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(gatewayUrl, appId, merchantPrivateKey, "json", "utf-8", alipayPublicKey, signType);
    }
}

package com.cwj.express.auth.config.thirdapi;

import com.baidu.aip.face.AipFace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn(value = "thirdApiConfig")
public class AipFaceConfig {
    private final ThirdApiConfig thirdApiConfig;

    @Autowired
    public AipFaceConfig(ThirdApiConfig thirdApiConfig) {
        this.thirdApiConfig = thirdApiConfig;
    }

    @Bean
    public AipFace aipFace(){
        BaiduFaceConfig baidu = thirdApiConfig.getBaidu();
        AipFace aipFace = new AipFace(baidu.getAppId(), baidu.getAppKey(), baidu.getSecretKey());
        aipFace.setConnectionTimeoutInMillis(baidu.getConnTimeout());
        aipFace.setSocketTimeoutInMillis(baidu.getSocketTimeOut());
        return aipFace;
    }
}

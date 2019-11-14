package com.cwj.express.auth.config.thirdapi;

import lombok.Data;

/**
 * @author cwj
 * 百度人脸api配置
 */
@Data
public class BaiduFaceConfig {
    private String appId;
    private String appKey;
    private String secretKey;
    private int connTimeout;
    private int socketTimeOut;
    private int acceptScore;
    private String groupId;
}

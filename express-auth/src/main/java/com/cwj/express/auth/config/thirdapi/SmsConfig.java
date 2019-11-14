package com.cwj.express.auth.config.thirdapi;

import lombok.Data;

/**
 * @author cwj
 * 腾讯短信配置
 */

@Data
public class SmsConfig{
    private Integer appId;
    private String appKey;
    private Integer templateId;
    private String sign;
    private String intervalMin;
    private String validMin;
}

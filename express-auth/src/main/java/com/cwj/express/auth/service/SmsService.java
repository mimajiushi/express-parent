package com.cwj.express.auth.service;


import com.cwj.express.common.model.response.ResponseResult;

/**
 * 短信业务
 */
public interface SmsService {
    /**
     * 发送短信
     * @param tel 手机号码
     * @param code 验证码
     */
    ResponseResult send(String tel, String code);

    /**
     * 校验短信
     */
    ResponseResult check(String tel, String code);
}

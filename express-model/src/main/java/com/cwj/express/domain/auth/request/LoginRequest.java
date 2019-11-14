package com.cwj.express.domain.auth.request;

import lombok.*;

import java.io.Serializable;

/**
 * Created by admin on 2018/3/5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest implements Serializable {

    /**
     * 用户名
     */
    String username;

    /**
     * 密码
     */
    String password;

    /**
     * 认证类型
     * - password: 账号密码 - phone: 短信 - face: 人脸
     */
    String authType;

    /**
     * 人脸数据
     */
    String faceDate;

    /**
     * 手机号
     */
    String phoneNum;

    /**
     * 短信验证码
     */
    String phoneCode;

    /**
     * 目前没什么用
     */
    String verifycode;

}

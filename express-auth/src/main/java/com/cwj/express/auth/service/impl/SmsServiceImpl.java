package com.cwj.express.auth.service.impl;

import com.cwj.express.auth.config.thirdapi.SmsConfig;
import com.cwj.express.auth.config.thirdapi.ThirdApiConfig;
import com.cwj.express.auth.service.RedisService;
import com.cwj.express.auth.service.SmsService;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

/**
 * 短信服务
 * @date 2019年04月20日 14:59
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SmsServiceImpl implements SmsService {

    private final RedisService redisService;
    private final ThirdApiConfig thirdApiConfig;

    @Override
    public ResponseResult send(String tel, String code) {
        SmsConfig smsConfig = thirdApiConfig.getSms();
        SmsSingleSender sender = new SmsSingleSender(smsConfig.getAppId(),smsConfig.getAppKey());
        ArrayList<String> params = new ArrayList<>();
        // 添加模板参数
        params.add(code);
        params.add(smsConfig.getValidMin());

        try {
            SmsSingleSenderResult result = sender
                    .sendWithParam("86", tel, smsConfig.getTemplateId(), params, smsConfig.getSign(), "", "");
            if(result.result == 0) {
                // redis存储
                String key = RedisConfig.SMS_HEAD+":"+tel;
                redisService.setKeyValTTL(key, code, RedisConfig.SMS_TIME_OUT);
                return ResponseResult.SUCCESS("发送验证码成功！");
            } else {
                // TODO 处理服务端错误码
                log.error("验证码发送失败，手机号：{}，错误信息：{}", tel, result.errMsg);
                return ResponseResult.FAIL(CommonCode.SEND_SMS_FAIL);
            }
        } catch (Exception e) {
            log.error("验证码发送出现异常：{}", e.getMessage());
            return ResponseResult.FAIL("验证码发送出现异常");
        }
    }

    @Override
    public ResponseResult check(String tel, String code) {
        String key = RedisConfig.SMS_HEAD+":"+tel;
        String res = redisService.get(key);
        if (StringUtils.isEmpty(res)){
            return ResponseResult.FAIL(CommonCode.SMS_CODE_TIMEOUT);
        }
        if (!res.equals(code)){
            return ResponseResult.FAIL(CommonCode.SMS_CODE_FAIL);
        }
        return ResponseResult.SUCCESS();
    }
}

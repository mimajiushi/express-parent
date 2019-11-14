package com.cwj.express.ucenter.service.impl;

import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.domain.ucenter.UserEvaluate;
import com.cwj.express.ucenter.dao.UserEvaluateMapper;
import com.cwj.express.ucenter.service.RedisService;
import com.cwj.express.ucenter.service.UserEvaluateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author cwj
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UserEvaluateServiceImpl implements UserEvaluateService {

    private final RedisService redisService;
    private final UserEvaluateMapper userEvaluateMapper;

    @Override
    public UserEvaluate getScoreById(String id) {
        String key = RedisConfig.USER_EVALUATE_DATA + ":" + id;
        // 从缓存获取
//        String value = redisService.get(key);
        // 缓存没有则数据库
//        if (StringUtils.isEmpty(value)){
            UserEvaluate userEvaluate = userEvaluateMapper.selectById(id);
//            if (!ObjectUtils.isEmpty(userEvaluate)){
//                redisService.set(key, JSON.toJSONString(userEvaluate));
//            }else {
//                 没有则默认满分
                userEvaluate.setScore(new BigDecimal(10));
//            }
            return userEvaluate;
//        }
//        return JSON.parseObject(value, UserEvaluate.class);
    }
}

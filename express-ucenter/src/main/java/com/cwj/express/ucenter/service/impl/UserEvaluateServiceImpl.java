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
import org.springframework.util.ObjectUtils;

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

        UserEvaluate userEvaluate = userEvaluateMapper.selectById(id);
        if (ObjectUtils.isEmpty(userEvaluate)) {
            userEvaluate = new UserEvaluate();
            userEvaluate.setScore(new BigDecimal(10));
            userEvaluate.setCount(0);
        }
        return userEvaluate;
    }
}

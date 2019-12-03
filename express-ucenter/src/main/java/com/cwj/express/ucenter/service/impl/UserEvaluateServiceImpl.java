package com.cwj.express.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.exception.CustomException;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.domain.ucenter.UserEvaluate;
import com.cwj.express.domain.ucenter.UserEvaluateEventLog;
import com.cwj.express.ucenter.dao.UserEvaluateEventLogMapper;
import com.cwj.express.ucenter.dao.UserEvaluateMapper;
import com.cwj.express.ucenter.service.RedisService;
import com.cwj.express.ucenter.service.UserEvaluateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author cwj
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UserEvaluateServiceImpl implements UserEvaluateService {

    private final RedisService redisService;
    private final UserEvaluateMapper userEvaluateMapper;
    private final UserEvaluateEventLogMapper userEvaluateEventLogMapper;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScoreAndCount(String userId, BigDecimal score, String logId) {
        // 先查询日志表，解决消息可能是重复发送的情况
        int logCount = userEvaluateEventLogMapper.selectCount(new QueryWrapper<UserEvaluateEventLog>()
                .eq("log_id", logId));
        if (logCount > 0){
            log.warn("【更改评分业务】更新前发现日志表有操作记录，判定此消息可能为重复发送消息，日志id:{}", logId);
            return;
        }else {
            // 没有记录则插入
            userEvaluateEventLogMapper.insert(UserEvaluateEventLog.builder()
                    .logId(logId)
                    .userId(userId)
                    .score(score)
                    .createDate(LocalDateTime.now()).build());
        }

        UserEvaluate userEvaluate = userEvaluateMapper.selectById(userId);
        if (userEvaluate.getCount() == 0){
            userEvaluate.setCount(1);
            userEvaluate.setScore(score);
            int count = userEvaluateMapper.updateById(userEvaluate);
            if (count < 1){
                ExceptionCast.cast(CommonCode.EVALUATE_UPDATE_RETRY);
            }
            return;
        }
        userEvaluateMapper.updateCount(userId);
        userEvaluateMapper.updateScore(userId, score);
    }
}

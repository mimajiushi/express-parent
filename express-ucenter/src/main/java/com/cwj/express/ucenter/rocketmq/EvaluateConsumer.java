package com.cwj.express.ucenter.rocketmq;

import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.ucenter.service.UserEvaluateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


/**
 * @author cwj
 * 评价之后校准评分
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RocketMQMessageListener(consumerGroup = RocketmqConfig.EVALUATE_SCORE_GROUP, topic = RocketmqConfig.EVALUATE_SCORE_TOPIC)
@Slf4j
public class EvaluateConsumer implements RocketMQListener<String> {

    private final UserEvaluateService userEvaluateService;

    @Override
    public void onMessage(String userIdAndScore) {
        String[] values = userIdAndScore.split("@@");
        String userId = values[0];
        BigDecimal score = new BigDecimal(values[1]);
        String logId = values[2];
        userEvaluateService.updateScoreAndCount(userId, score, logId);
    }
}

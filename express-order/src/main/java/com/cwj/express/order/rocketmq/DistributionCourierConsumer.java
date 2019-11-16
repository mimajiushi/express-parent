package com.cwj.express.order.rocketmq;

import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.order.feignclient.ucenter.UcenterFeignClient;
import com.cwj.express.order.service.DistributionCourierService;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.order.service.RedisService;
import com.cwj.express.utils.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cwj
 * 延时消息取消清单和关闭支付
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RocketMQMessageListener(consumerGroup = RocketmqConfig.DISTRIBUTION_COURIER_GROUP, topic = RocketmqConfig.DISTRIBUTION_COURIER_TOPIC)
@Slf4j
public class DistributionCourierConsumer implements RocketMQListener<String> {

    private final DistributionCourierService distributionCourierService;
    private final RedisService redisService;

    @Override
    public void onMessage(String orderId) {
        // 分配配送员业务
        distributionCourierService.distributionCourier(orderId);

        // 删除redis日志 （可以不删）
        String logKey = RedisConfig.ORDER_COURIER_DATA + "::" + orderId;
        redisService.remove(logKey);
    }
}

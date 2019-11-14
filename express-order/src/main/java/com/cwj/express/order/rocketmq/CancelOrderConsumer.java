package com.cwj.express.order.rocketmq;

import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.utils.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author cwj
 * 延时消息取消清单和关闭支付
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RocketMQMessageListener(consumerGroup = RocketmqConfig.CANCEL_ORDER_GROUP, topic = RocketmqConfig.EXPRESS_TOPIC)
@Slf4j
public class CancelOrderConsumer implements RocketMQListener<String> {

    private final OrderInfoService orderInfoService;

    @Override
    public void onMessage(String orderIdAndUserId) {
        String[] params = orderIdAndUserId.split("@@");
        String orderId = params[0];
        String userId = params[1];
        LocalDateTime timeVersion = LocalDateTimeUtils.ymdhmsParseToLocalDataTime(params[2]);
        orderInfoService.cancelOrder(orderId, userId, timeVersion);
    }
}

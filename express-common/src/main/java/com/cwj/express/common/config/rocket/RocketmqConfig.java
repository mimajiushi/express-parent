package com.cwj.express.common.config.rocket;

import lombok.Getter;
import lombok.ToString;

/**
 * 本项目 rocketmq 的 destination 和 group 配置
 */
@Getter
@ToString
public class RocketmqConfig {
    /**
     * 配置订阅、发布主题
     */
    public static final String CANCEL_ORDER_TOPIC = "CANCEL_ORDER_TOPIC";
    public static final String DISTRIBUTION_COURIER_TOPIC = "DISTRIBUTION_COURIER_TOPIC";

    /**
     * 配置取消订单消息组
     */
    public static final String CANCEL_ORDER_GROUP = "CANCEL_ORDER_GROUP";

    /**
     * 配置分配配送员消息组
     */
    public static final String DISTRIBUTION_COURIER_GROUP = "DISTRIBUTION_COURIER_GROUP";
}

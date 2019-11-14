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
    public static final String EXPRESS_TOPIC = "EXPRESS_TOPIC";

    /**
     * 配置组
     */
    public static final String CANCEL_ORDER_GROUP = "CANCEL_ORDER_GROUP";
}

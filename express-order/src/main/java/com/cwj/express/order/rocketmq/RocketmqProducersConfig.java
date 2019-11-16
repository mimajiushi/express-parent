package com.cwj.express.order.rocketmq;

import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rocketmq多主题配置
 * 注意setInstanceName()，整个应用中名字不能重复
 */

@Configuration
public class RocketmqProducersConfig {

    @Value("${rocketmq.name-server}")
    private String nameServer;
    @Value("${rocketmq.producer.send-message-timeout}")
    private int sendTimeOut;
    @Value("${rocketmq.producer.compress-message-body-threshold}")
    private int compressMsgBodyOverHowmuch;
    @Value("${rocketmq.producer.max-message-size}")
    private int maxMessageSize;


    private DefaultMQProducer cancelOrderProducer() throws MQClientException {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(RocketmqConfig.CANCEL_ORDER_GROUP);
        defaultMQProducer.setInstanceName("cancelOrderProducer");
        defaultMQProducer.setNamesrvAddr(nameServer);
        defaultMQProducer.setSendMsgTimeout(sendTimeOut);
        defaultMQProducer.setCompressMsgBodyOverHowmuch(compressMsgBodyOverHowmuch);
        defaultMQProducer.setMaxMessageSize(maxMessageSize);
        return defaultMQProducer;
    }

    private DefaultMQProducer distribuitionCourierProducer() throws MQClientException {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(RocketmqConfig.DISTRIBUTION_COURIER_GROUP);
        defaultMQProducer.setInstanceName("distribuitionCourierProducer");
        defaultMQProducer.setNamesrvAddr(nameServer);
        defaultMQProducer.setSendMsgTimeout(sendTimeOut);
        defaultMQProducer.setCompressMsgBodyOverHowmuch(compressMsgBodyOverHowmuch);
        defaultMQProducer.setMaxMessageSize(maxMessageSize);
        return defaultMQProducer;
    }

    @Bean("cancelOrder")
    public RocketMQTemplate cancelOrder() throws MQClientException {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(cancelOrderProducer());
        rocketMQTemplate.setObjectMapper(new ObjectMapper());
        return rocketMQTemplate;
    }

    @Bean("distribuitionCourier")
    public RocketMQTemplate distribuitionCourier() throws MQClientException {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(distribuitionCourierProducer());
        rocketMQTemplate.setObjectMapper(new ObjectMapper());
        return rocketMQTemplate;
    }

}

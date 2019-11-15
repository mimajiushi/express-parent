package com.cwj.express.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.common.enums.*;
import com.cwj.express.common.enums.rocketmq.MessageDelayLevel;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.order.config.alipay.AliPayConfig;
import com.cwj.express.order.dao.OrderInfoMapper;
import com.cwj.express.order.dao.OrderPaymentMapper;
import com.cwj.express.order.feignclient.ucenter.UcenterFeignClient;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.order.service.OrderPaymentService;
import com.cwj.express.order.service.RedisService;
import com.cwj.express.utils.ExpressOauth2Util;
import com.cwj.express.utils.LocalDateTimeUtils;
import com.cwj.express.vo.order.OrderDashboardVO;
import com.cwj.express.vo.order.OrderInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.omg.CORBA.TIMEOUT;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    private final OrderInfoMapper orderInfoMapper;
    private final OrderPaymentService orderPaymentService;
    private final OrderPaymentMapper orderPaymentMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisService redisService;
    private final AliPayConfig aliPayConfig;
    private final UcenterFeignClient ucenterFeignClient;

    @Value("${rocketmq.producer.send-message-timeout}")
    private Long timeout;

    @Override
    @Cacheable(cacheNames = RedisConfig.ORDER_INFO_DASHBOARD_DATA, key = "#userId")
    public OrderDashboardVO getUserDashboardData(String userId) {
        int waitPayMentCount = 0, waitCount = 0, transportCount = 0;
        List<OrderInfo> orderInfos = orderInfoMapper.selectList(
                new QueryWrapper<OrderInfo>().eq("user_id", userId));

        for (OrderInfo orderInfo : orderInfos) {
            switch (orderInfo.getOrderStatus()){
                // 待接单的，待接单的也有可能是待支付的
                case WAIT_DIST:
                    waitCount++;
                    OrderPayment orderPayment = orderPaymentService.getByOrderId(orderInfo.getId());
                    if (PaymentStatusEnum.WAIT_BUYER_PAY == orderPayment.getPaymentStatus()){
                        waitPayMentCount++;
                    }
                    break;
                // 待配送的
                case TRANSPORT:
                    transportCount++;
                    break;
                default:
                    break;
            }
        }

        return OrderDashboardVO.builder().
                    waitCount(waitCount).
                    waitPaymentCount(waitPayMentCount).
                    transportCount(transportCount)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRED ,rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisConfig.ORDER_INFO_DASHBOARD_DATA, key = "#userId")
    @Override
    public ResponseResult createOrder(OrderInfoVO orderInfoVO, String userId) {
        // 有时候格式化之后的时间会出现差一秒的情况，所以一来一回的转换用作担保
        LocalDateTime tempnow = LocalDateTime.now();
        String timeString = LocalDateTimeUtils.formatToYMDHMS(tempnow);
        LocalDateTime now = LocalDateTimeUtils.ymdhmsParseToLocalDataTime(timeString);
        // 创建订单持久化对象
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderInfoVO, orderInfo);

        orderInfo.setUserId(userId);
        orderInfo.setOrderTypeEnum(OrderTypeEnum.getByType(orderInfoVO.getType()));
        orderInfo.setOrderStatus(OrderStatusEnum.WAIT_DIST);
        orderInfo.setCourierRemark("");
        orderInfo.setHasDelete(0);
        orderInfo.setDeleteType(OrderDeleteEnum.NONE);
        orderInfo.setCreateDate(now);
        orderInfo.setUpdateDate(now);
        //计算价格
        BigDecimal totalPrice = new BigDecimal(2);
        // < 0.5kg都是2元
        // > 0.5 && < 1 3元
        // > 1 重量向上取整 每kg4元
        // todo 之后再考虑如何动态调整价格,课设文档也没这需求，而且这个价格理论上是长期不变的
        double weight = orderInfoVO.getWeight();
        if (weight < 0.5){
            totalPrice = new BigDecimal(2);
        }else if (weight < 1){
            totalPrice = new BigDecimal(3);
        }else if (weight >= 1){
            // 这里单价先写死 单价：1kg/4元
            totalPrice = BigDecimal.valueOf(4 * Math.ceil(weight));
        }

        // 计算折扣后的价格，需要远程调用
        SysRolesLevel roleMsgByUserId = ucenterFeignClient.getRoleMsgByUserId();
        BigDecimal discount = roleMsgByUserId.getDiscount();
        totalPrice = totalPrice.multiply(discount);
//        orderInfo.setPrice(totalPrice);
        // 保存数据库
        boolean success1 = SqlHelper.retBool(orderInfoMapper.insert(orderInfo));
        // 保存订单支付记录
        boolean success2 = SqlHelper.retBool(orderPaymentMapper.insert(
                OrderPayment.builder().
                        orderId(orderInfo.getId()).
                        paymentStatus(PaymentStatusEnum.WAIT_BUYER_PAY).
                        paymentType(PaymentTypeEnum.AliPay).
                        payment(totalPrice).
                        createDate(now).
                        updateDate(now).
                        seller(aliPayConfig.getUid()).build()
        ));
        if (success1 && success2){
            // 发送10分钟延时消息
            rocketMQTemplate.syncSend(RocketmqConfig.EXPRESS_TOPIC,
                    MessageBuilder.withPayload(orderInfo.getId() + "@@" + userId + "@@" + timeString).build()
                    , timeout, MessageDelayLevel.TIME_10M.level);
            // 设置ttl为延时消息时间（10分钟）的redis缓存
            String key = RedisConfig.ORDER_INFO_DATA + "::" + orderInfo.getId() + userId;
            redisService.setKeyValTTL(key, JSON.toJSONString(orderInfo), RedisConfig.CREATE_ORDER_TTL);
            return ResponseResult.SUCCESS(orderInfo.getId());
        }
        return ResponseResult.FAIL();
    }

    @Override
    public OrderInfo getOrderByIdAndUserId(String orderId, String userId) {
        String key = RedisConfig.ORDER_INFO_DATA + "::" + orderId + userId;
        String value = redisService.get(key);
        if (StringUtils.isEmpty(value)){
            OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
            redisService.setKeyValTTL(key, JSON.toJSONString(orderInfo), RedisConfig.ORDER_INFO_TTL);
            return orderInfo;
        }
        return JSON.parseObject(value, OrderInfo.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisConfig.ORDER_INFO_DASHBOARD_DATA, key = "#userId")
    public void cancelOrder(String orderId, String userId, LocalDateTime timeVersion) {
        OrderInfo orderInfo = OrderInfo.builder().id(orderId).orderStatus(OrderStatusEnum.CANCEL).updateDate(timeVersion).build();
        OrderPayment orderPayment = OrderPayment.builder().orderId(orderId).paymentStatus(PaymentStatusEnum.TRADE_CLOSED).updateDate(timeVersion).build();
        // 这里使用乐观锁更新，支付成功则是强制更新
        boolean success1 = SqlHelper.retBool(orderPaymentMapper.updateById(orderPayment));
        if (success1){
            boolean success2 = SqlHelper.retBool(orderInfoMapper.updateById(orderInfo));
            if (success2){
                // 清除redis缓存
                String key = RedisConfig.ORDER_INFO_DATA + "::" + orderId + userId;
                redisService.remove(key);
            }
        }
    }

    @Override
    @Transactional
    public void updateOrder(OrderInfo orderInfo) {
        //清除订单详情缓存
        String key = RedisConfig.ORDER_INFO_DATA + "::" + orderInfo.getId() + orderInfo.getUserId();
        redisService.remove(key);
        orderInfo.setUserId(null);
        orderInfoMapper.updateById(orderInfo);
    }
}

package com.cwj.express.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.common.enums.OrderStatusEnum;
import com.cwj.express.common.enums.PaymentStatusEnum;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.order.config.alipay.AliPayConfig;
import com.cwj.express.order.dao.OrderInfoMapper;
import com.cwj.express.order.dao.OrderPaymentMapper;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.order.service.OrderPaymentService;
import com.cwj.express.order.service.RedisService;
import com.cwj.express.vo.order.UpdateOrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class OrderPaymentServiceImpl implements OrderPaymentService {

    private final OrderPaymentMapper orderPaymentMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final AliPayConfig aliPayConfig;
//    @Qualifier("distribuitionCourier")
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisService redisService;


    @Override
//    @Cacheable(cacheNames = RedisConfig.ORDER_PAYMENT_DATA, key = "#orderId")
    public OrderPayment getByOrderId(String orderId) {
        return orderPaymentMapper.selectById(orderId);
    }

    @Override
    public boolean validAlipay(Map<String, String> params) throws AlipayApiException {
        /* 实际验证过程建议商户务必添加以下校验：
        1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
        2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
        4、验证app_id是否为该商户本身。
        */

        // 1、调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAlipayPublicKey(), "utf-8", aliPayConfig.getSignType());
        if(!signVerified) {
            return false;
        }
        // 获取订单数据
        String orderId = params.get("out_trade_no");
        OrderPayment payment = getByOrderId(orderId);
        if(ObjectUtils.isEmpty(payment)) {
            return false;
        }
        // 2、判断金额是否相等
        BigDecimal money = new BigDecimal(params.get("total_amount"));
        if(money.compareTo(payment.getPayment()) != 0) {
            return false;
        }

        // 3、判断商户ID是否相等
        String sellerId = params.get("seller_id");
        if(!sellerId.equals(payment.getSeller())) {
            return false;
        }

        // 4、判断APP_ID是否相等
        String appId = params.get("app_id");
        if(!aliPayConfig.getAppId().equals(appId)) {
            return false;
        }

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisConfig.ORDER_INFO_DASHBOARD_DATA, key = "#userId")
    @Override
    public void updatePayment(UpdateOrderVo orderVo, String userId) {
        // 此处更新不能使用乐观锁，否则可能出现被订单取消业务锁后更新无效
        OrderInfo orderInfo = OrderInfo.builder().
                id(orderVo.getOrderId()).
                userId(userId).
                orderStatus(orderVo.getOrderStatusEnum()).build();
        updateOrderInfoById(orderInfo);
        OrderPayment orderPayment = OrderPayment.builder().
                orderId(orderVo.getOrderId()).
                paymentId(orderVo.getTrade_no()).
                paymentStatus(orderVo.getPaymentStatusEnum()).build();
        updatePaymentByid(orderPayment);
        // 付款成功则发送安排配送员的mq
//        if (PaymentStatusEnum.TRADE_SUCCESS == orderVo.getPaymentStatusEnum()){
//            rocketMQTemplate.sendMessageInTransaction(
//                    RocketmqConfig.DISTRIBUTION_COURIER_GROUP,
//                    RocketmqConfig.DISTRIBUTION_COURIER_TOPIC,
//                    MessageBuilder.withPayload().setHeader("orderId", orderInfo.getId()).build(),
//                    orderInfo.getId()
//            );
//        }
    }

    /**
     * 暂时不考虑宕机可能性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearOrderCourier(String orderId, String courierId, String schoolId) {
        String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + schoolId;
        Double zscore = redisService.zscore(key, courierId);
        OrderInfo orderInfo = OrderInfo.builder().id(orderId).courierId("").build();
        orderInfoMapper.updateById(orderInfo);
        if (!ObjectUtils.isEmpty(zscore)){
            redisService.increment(key, courierId, RedisConfig.COURIER_SCORE);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePaymentByid(OrderPayment orderPayment){
        orderPaymentMapper.updateById(orderPayment);
        // 函数嵌套时不能再用注解针对key删除，只能自己手动删
        String key = RedisConfig.ORDER_PAYMENT_DATA + "::" + orderPayment.getOrderId();
        redisService.remove(key);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateOrderInfoById(OrderInfo orderInfo){
        //清除订单详情缓存
        String key = RedisConfig.ORDER_INFO_DATA + "::" + orderInfo.getId() + orderInfo.getUserId();
        redisService.remove(key);
        orderInfo.setUserId(null);
        orderInfoMapper.updateById(orderInfo);
    }
}

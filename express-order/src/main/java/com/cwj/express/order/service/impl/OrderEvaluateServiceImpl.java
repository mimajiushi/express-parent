package com.cwj.express.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.order.OrderEvaluate;
import com.cwj.express.order.dao.OrderEvaluateMapper;
import com.cwj.express.order.service.OrderEvaluateService;
import com.cwj.express.utils.LocalDateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class OrderEvaluateServiceImpl implements OrderEvaluateService {

    private final OrderEvaluateMapper orderEvaluateMapper;

    @Override
//    @Cacheable(cacheNames = RedisConfig.COUNT_EVALUATE_DATA, key = "#id")
    public int countEvaluate(String id, Integer roleId) {
        Integer count = 0;
        if (SysRoleEnum.USER.getType() == roleId){
            count = orderEvaluateMapper.selectCount(
                    new QueryWrapper<OrderEvaluate>().eq("user_id", id).ne("courier_evaluate", ""));
        }else if (SysRoleEnum.COURIER.getType() == roleId){
            count = orderEvaluateMapper.selectCount(
                    new QueryWrapper<OrderEvaluate>().eq("courier_id", id).ne("user_evaluate", ""));
        }
        return count;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult evaluate(String orderId, String userId, BigDecimal score, String evaluate, SysRoleEnum roleEnum) {
        // 判断订单是否已评价
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime defaultTime = LocalDateTime.of(1111, 11, 11, 11, 11, 11, 0);
        QueryWrapper<OrderEvaluate> orderEvaluateQueryWrapper = new QueryWrapper<>();
        orderEvaluateQueryWrapper.eq("id", orderId);
        OrderEvaluate orderEvaluate = orderEvaluateMapper.selectById(orderId);
        if (ObjectUtils.isEmpty(orderEvaluate)){
            orderEvaluate = OrderEvaluate.builder()
                    .id(orderId).build();
            if (SysRoleEnum.COURIER == roleEnum){
                orderEvaluate.setCourierId(userId);
                orderEvaluate.setCourierEvaluate(evaluate);
                orderEvaluate.setCourierScore(score);
                orderEvaluate.setCourierDate(now);
            }else {
                orderEvaluate.setUserId(userId);
                orderEvaluate.setUserDate(now);
                orderEvaluate.setUserScore(score);
                orderEvaluate.setUserEvaluate(evaluate);
            }
            orderEvaluateMapper.insert(orderEvaluate);
            return ResponseResult.SUCCESS();
        }
        if (SysRoleEnum.COURIER == roleEnum){
            if (userId.equals(orderEvaluate.getCourierId())){
                return ResponseResult.FAIL(CommonCode.ORDER_EVALUATE_EXIST);
            }
            orderEvaluate.setCourierId(userId);
            orderEvaluate.setCourierEvaluate(evaluate);
            orderEvaluate.setCourierScore(score);
            orderEvaluate.setCourierDate(now);
            orderEvaluateQueryWrapper.eq("courier_date", defaultTime);
        }else {
            if (userId.equals(orderEvaluate.getCourierId())) {
                return ResponseResult.FAIL(CommonCode.ORDER_EVALUATE_EXIST);
            }
            orderEvaluate.setUserId(userId);
            orderEvaluate.setUserDate(now);
            orderEvaluate.setUserScore(score);
            orderEvaluate.setUserDate(now);
            orderEvaluateQueryWrapper.eq("user_date", defaultTime);
        }
        int count = orderEvaluateMapper.update(orderEvaluate, orderEvaluateQueryWrapper);
        if (count < 1){
            return ResponseResult.FAIL(CommonCode.ORDER_HAS_BEEN_CHANGEED);
        }
        return ResponseResult.SUCCESS();
    }

    @Override
    public OrderEvaluate getById(String orderId) {
        return orderEvaluateMapper.selectById(orderId);
    }
}

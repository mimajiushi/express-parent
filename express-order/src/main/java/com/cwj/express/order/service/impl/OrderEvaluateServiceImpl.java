package com.cwj.express.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.order.OrderEvaluate;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.order.dao.OrderEvaluateMapper;
import com.cwj.express.order.service.OrderEvaluateService;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.utils.LocalDateTimeUtils;
import com.cwj.express.vo.order.OrderEvaluateItemVO;
import com.cwj.express.vo.order.OrderEvaluateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class OrderEvaluateServiceImpl implements OrderEvaluateService {

    private final OrderEvaluateMapper orderEvaluateMapper;
    private final OrderInfoService orderInfoService;

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
        OrderInfo orderInfo = orderInfoService.getOrderById(orderId);
        if (ObjectUtils.isEmpty(orderEvaluate)){
            orderEvaluate = OrderEvaluate.builder()
                    .id(orderId).build();
            if (SysRoleEnum.COURIER == roleEnum){
                orderEvaluate.setCourierId(userId);
                orderEvaluate.setUserId(orderInfo.getUserId());
                orderEvaluate.setCourierEvaluate(evaluate);
                orderEvaluate.setCourierScore(score);
                orderEvaluate.setCourierDate(now);
            }else {
                orderEvaluate.setUserId(userId);
                orderEvaluate.setCourierId(orderInfo.getCourierId());
                orderEvaluate.setUserDate(now);
                orderEvaluate.setUserScore(score);
                orderEvaluate.setUserEvaluate(evaluate);
            }
            orderEvaluateMapper.insert(orderEvaluate);
            return ResponseResult.SUCCESS();
        }
        if (SysRoleEnum.COURIER == roleEnum){
            if (!StringUtils.isEmpty(orderEvaluate.getCourierEvaluate())){
                return ResponseResult.FAIL(CommonCode.ORDER_EVALUATE_EXIST);
            }
//            orderEvaluate.setCourierId(userId);
//            orderEvaluate.setUserId(orderInfo.getUserId());
            orderEvaluate.setCourierEvaluate(evaluate);
            orderEvaluate.setCourierScore(score);
            orderEvaluate.setCourierDate(now);
            orderEvaluateQueryWrapper.eq("courier_date", defaultTime);
        }else {
            if (!StringUtils.isEmpty(orderEvaluate.getUserEvaluate())) {
                return ResponseResult.FAIL(CommonCode.ORDER_EVALUATE_EXIST);
            }
//            orderEvaluate.setUserId(userId);
//            orderInfo.setCourierId(orderInfo.getCourierId());
            orderEvaluate.setUserDate(now);
            orderEvaluate.setUserScore(score);
            orderEvaluate.setUserEvaluate(evaluate);
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

    @Override
    public OrderEvaluateVO getPageByUserId(Page<OrderEvaluate> page, String userId, SysRoleEnum roleEnum) {
        QueryWrapper<OrderEvaluate> orderEvaluateQueryWrapper = new QueryWrapper<>();
        if (roleEnum == SysRoleEnum.COURIER){
            orderEvaluateQueryWrapper.eq("courier_id", userId).orderByDesc("update_date");
        }else {
            orderEvaluateQueryWrapper.eq("user_id", userId).orderByDesc("update_date");
        }
        IPage<OrderEvaluate> resPage = orderEvaluateMapper.selectPage(page, orderEvaluateQueryWrapper);
        List<OrderEvaluateItemVO> evaluateItemVOS = converter(resPage.getRecords(), roleEnum);
        return OrderEvaluateVO.builder()
                .current(page.getCurrent())
                .page(resPage.getPages())
                .record(evaluateItemVOS).build();
    }

    private List<OrderEvaluateItemVO> converter(List<OrderEvaluate> evaluateList, SysRoleEnum roleEnum){
        if (ObjectUtils.isEmpty(evaluateList)){
            return new ArrayList<>();
        }
        boolean isCourier = roleEnum == SysRoleEnum.COURIER;
        return evaluateList.stream().map(item -> {
            OrderEvaluateItemVO orderEvaluateItemVO = new OrderEvaluateItemVO();
            orderEvaluateItemVO.setOrderId(item.getId());
            String evaluate = isCourier?item.getUserEvaluate():item.getCourierEvaluate();
            BigDecimal score = isCourier?item.getUserScore():item.getCourierScore();
            orderEvaluateItemVO.setEvaluate(evaluate);
            orderEvaluateItemVO.setScore(score);
            return orderEvaluateItemVO;
        }).collect(Collectors.toList());
    }
}

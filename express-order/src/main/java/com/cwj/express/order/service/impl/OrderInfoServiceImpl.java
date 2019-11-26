package com.cwj.express.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.common.enums.*;
import com.cwj.express.common.enums.rocketmq.MessageDelayLevel;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.area.DataCompany;
import com.cwj.express.domain.area.DataSchool;
import com.cwj.express.domain.order.OrderEvaluate;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.order.config.alipay.AliPayConfig;
import com.cwj.express.order.dao.OrderEvaluateMapper;
import com.cwj.express.order.dao.OrderInfoMapper;
import com.cwj.express.order.dao.OrderPaymentMapper;
import com.cwj.express.order.feignclient.area.AreaFeignClient;
import com.cwj.express.order.feignclient.ucenter.UcenterFeignClient;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.order.service.OrderPaymentService;
import com.cwj.express.order.service.RedisService;
import com.cwj.express.utils.LocalDateTimeUtils;
import com.cwj.express.vo.order.OrderDashboardVO;
import com.cwj.express.vo.order.OrderDetailVO;
import com.cwj.express.vo.order.OrderHistoryVO;
import com.cwj.express.vo.order.OrderInfoVO;
import com.cwj.express.vo.table.BootstrapTableVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
public class OrderInfoServiceImpl implements OrderInfoService {

    private final OrderInfoMapper orderInfoMapper;
    private final OrderPaymentService orderPaymentService;
    private final OrderPaymentMapper orderPaymentMapper;
    private final OrderEvaluateMapper orderEvaluateMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisService redisService;
    private final AliPayConfig aliPayConfig;
    private final UcenterFeignClient ucenterFeignClient;
    private final AreaFeignClient areaFeignClient;

    @Value("${rocketmq.producer.send-message-timeout}")
    private Long timeout;

    @Override
//    @Cacheable(cacheNames = RedisConfig.ORDER_INFO_DASHBOARD_DATA, key = "#userId")
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

    @Override
    public OrderDashboardVO getCourerDashboardData(String courierId) {
        // 获取等待揽收的订单数量
        int waitPickupCount = orderInfoMapper.selectCount(new QueryWrapper<OrderInfo>()
                .eq("courier_id", courierId)
                .eq("status", OrderStatusEnum.WAIT_PICK_UP.getStatus()));
        // 获取等待配送的订单数量
        int transportCount = orderInfoMapper.selectCount(new QueryWrapper<OrderInfo>()
                .eq("courier_id", courierId)
                .eq("status", OrderStatusEnum.TRANSPORT.getStatus()));
        return OrderDashboardVO.builder()
                .transportCount(transportCount)
                .waitPickUpCount(waitPickupCount).build();

    }

    @Transactional(propagation = Propagation.REQUIRED ,rollbackFor = Exception.class)
//    @CacheEvict(cacheNames = RedisConfig.ORDER_INFO_DASHBOARD_DATA, key = "#userId")
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
            rocketMQTemplate.syncSend(RocketmqConfig.CANCEL_ORDER_TOPIC,
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
//    @CacheEvict(cacheNames = RedisConfig.ORDER_INFO_DASHBOARD_DATA, key = "#userId")
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
    public BootstrapTableVO<OrderHistoryVO> orderList(Page<OrderInfo> page, String userId, SysRoleEnum roleEnum, OrderHistoryVO orderHistoryVO) {
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        List<OrderHistoryVO> rows = new ArrayList<>();
        if (-1 != orderHistoryVO.getOrderStatus()){
            orderInfoQueryWrapper.eq("status", OrderStatusEnum.getByStatus(orderHistoryVO.getOrderStatus()));
        }
        if (!StringUtils.isEmpty(orderHistoryVO.getId())){
            orderInfoQueryWrapper.eq("id", orderHistoryVO.getId());
        }
        if (!StringUtils.isEmpty(orderHistoryVO.getStartDate())){
            orderInfoQueryWrapper.ge("create_date", orderHistoryVO.getStartDate());
        }
        if (!StringUtils.isEmpty(orderHistoryVO.getEndDate())){
            orderInfoQueryWrapper.le("create_date", orderHistoryVO.getEndDate());
        }
        switch (roleEnum){
            // 管理员
            case ADMIN:
                if (-1 != orderHistoryVO.getPaymentStatus()){
                    // todo 根据支付状态查询
                }
                break;
            // 配送员
            case COURIER:
                orderInfoQueryWrapper.eq("courier_id", userId);
                IPage<OrderInfo> courierPages = orderInfoMapper.selectPage(page, orderInfoQueryWrapper);
                rows = converter(courierPages.getRecords(), userId, roleEnum);
                return BootstrapTableVO.<OrderHistoryVO>builder().rows(rows).total(courierPages.getTotal()).build();
            // 其它角色即付费角色
            default:
                orderInfoQueryWrapper.eq("user_id", userId);
                IPage<OrderInfo> iPage = orderInfoMapper.selectPage(page, orderInfoQueryWrapper);
                rows = converter(iPage.getRecords(), userId, roleEnum);
                return BootstrapTableVO.<OrderHistoryVO>builder().rows(rows).total(iPage.getTotal()).build();
        }
        return null;
    }

    @Override
    public OrderDetailVO orderDetail(String orderId, String userId, SysRoleEnum roleEnum) {
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        orderInfoQueryWrapper.eq("id", orderId);
        switch (roleEnum){
            case ADMIN:
                break;
            case COURIER:
                orderInfoQueryWrapper.eq("courier_id",userId);
                break;
            default:
                orderInfoQueryWrapper.eq("user_id", userId);
                break;
        }
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderInfoQueryWrapper);
        return getOrderDetail(orderInfo);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean pickUpOrder(String orderId, String courierId, String courierRemark) {
        // 乐观锁更新
        OrderInfo orderInfo = orderInfoMapper.selectOne(new QueryWrapper<OrderInfo>()
                .eq("id", orderId)
                .eq("courier_id", courierId)
                .eq("status", OrderStatusEnum.WAIT_PICK_UP.getStatus()));
        orderInfo.setOrderStatus(OrderStatusEnum.TRANSPORT);
        orderInfo.setCourierRemark(courierRemark);
        int count = orderInfoMapper.updateById(orderInfo);
        return count > 0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean finishOrder(String orderId, String courierId, String courierRemark) {
        SysUser courier = ucenterFeignClient.getById(courierId);
        String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + courier.getSchoolId();
        OrderInfo orderInfo = orderInfoMapper.selectOne(new QueryWrapper<OrderInfo>()
                .eq("id", orderId)
                .eq("courier_id", courierId)
                .eq("status", OrderStatusEnum.TRANSPORT.getStatus()));
        orderInfo.setOrderStatus(OrderStatusEnum.COMPLETE);
        orderInfo.setCourierRemark(courierRemark);
        // 完成订单，同样是乐观锁
        int count = orderInfoMapper.updateById(orderInfo);
        boolean success = count > 0;
        // 操作成功则加回配送员分数
        if (success){
            redisService.increment(key, courierId, RedisConfig.COURIER_SCORE);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setOrderExcetion(String orderId, String courierId, String courierRemark) {
        SysUser courier = ucenterFeignClient.getById(courierId);
        String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + courier.getSchoolId();
        OrderInfo orderInfo = orderInfoMapper.selectOne(new QueryWrapper<OrderInfo>()
                .eq("id", orderId)
                .eq("courier_id", courierId)
                .in("status", OrderStatusEnum.WAIT_PICK_UP.getStatus(), OrderStatusEnum.TRANSPORT.getStatus()));
        orderInfo.setOrderStatus(OrderStatusEnum.ERROR);
        orderInfo.setCourierRemark(courierRemark);
        // 设置订单异常，同样是乐观锁
        int count = orderInfoMapper.updateById(orderInfo);
        boolean success = count > 0;
        // 操作成功则加回配送员分数
        if (success){
            redisService.increment(key, courierId, RedisConfig.COURIER_SCORE);
        }
        return success;
    }

    @Override
    public Double countCourierScore(String courierId) {
        Integer count = 0;
        count = orderInfoMapper.selectCount(new QueryWrapper<OrderInfo>()
                .eq("courier_id", courierId)
                .eq("has_delete", 0)
                .in("status", OrderStatusEnum.WAIT_PICK_UP.getStatus(), OrderStatusEnum.TRANSPORT.getStatus()));
        return (RedisConfig.COURIER_MAX_SCORE - count*RedisConfig.COURIER_SCORE);
    }


    @Override
    public List<OrderInfo> getOrderByIdAndStatus(String[] orderids, Integer... status) {
        return orderInfoMapper.selectList(new QueryWrapper<OrderInfo>().in("id", orderids).in("status", status));
    }

    @Override
    public OrderInfo getOrderById(String orderId) {
        return orderInfoMapper.selectById(orderId);
    }

    /**
     * 查询封装返回的订单详情
     */
    private OrderDetailVO getOrderDetail(OrderInfo orderInfo){
        if (ObjectUtils.isEmpty(orderInfo)){
            return null;
        }
        // 获取寄件人信息，远程调用(学校id及手机号码)
        SysUser user = ucenterFeignClient.getById(orderInfo.getUserId());
        SysUser courier = ucenterFeignClient.getById(orderInfo.getCourierId());
        // 获取快递公司信息和学校信息，远程调用
        DataCompany company = areaFeignClient.getCompanyById(orderInfo.getCompany());
        DataSchool school = areaFeignClient.getSchoolInfoById(String.valueOf(user.getSchoolId()));
        // 获取支付信息
        OrderPayment payment = getPaymenyById(orderInfo.getId());
        // 基础订单信息
        return OrderDetailVO.builder()
                .orderId(orderInfo.getId())
                .orderStatus(orderInfo.getOrderStatus().getName())
                .serverType(orderInfo.getOrderTypeEnum().getDesc())
                .userRemark(orderInfo.getRemark())
                .schoolName(school.getName())
                .recName(orderInfo.getRecName())
                .recTel(orderInfo.getRecTel())
                .recAddress(orderInfo.getRecAddress())
                .senderName(user.getRealName())
                .senderTel(user.getTel())
                .senderAddress(orderInfo.getAddress())
                .odd(orderInfo.getOdd())
                .companyName(company.getName())
                .paymentType(payment.getPaymentType().getName())
                .payment(String.valueOf(payment.getPayment()))
                .paymentStatus(payment.getPaymentStatus().getName())
                .courierName(null != courier? courier.getRealName():"无")
                .courierTel(null != courier? courier.getTel():"无")
                .courierRemark(orderInfo.getCourierRemark()).build();
    }

    /**
     * 查询封装返回的订单列表
     */
    private List<OrderHistoryVO> converter(List<OrderInfo> orderInfoList, String userId, SysRoleEnum roleEnum){
        if (ObjectUtils.isEmpty(orderInfoList)){
            return new ArrayList<>();
        }
        return orderInfoList.stream().map(item -> {
            OrderHistoryVO orderHistoryVO1 = new OrderHistoryVO();
            BeanUtils.copyProperties(item, orderHistoryVO1);
            // 服务类型
            orderHistoryVO1.setServerType(item.getOrderTypeEnum().getDesc());
            // 获取支付信息
            OrderPayment payment = getPaymenyById(item.getId());
            // 查看订单是否能评分
            orderHistoryVO1.setCanScore(canEvaluate(item.getId(), userId, roleEnum));
            // 支付状态
            orderHistoryVO1.setPaymentStatus(payment.getPaymentStatus().getStatus());
            // 支付金额
            orderHistoryVO1.setPayment(String.valueOf(payment.getPayment()));
            // 订单状态
            orderHistoryVO1.setOrderStatus(item.getOrderStatus().getStatus());
            // 查询快递公司
            orderHistoryVO1.setCompany(areaFeignClient.getCompanyById(item.getCompany()).getName());
            return orderHistoryVO1;
        }).collect(Collectors.toList());
    }

    /**
     * 查询订单是否能评分（付费角色和配送员）
     */
    private String canEvaluate(String orderId, String userId, SysRoleEnum roleEnum){
        if (roleEnum == SysRoleEnum.COURIER){
            // 配送员
            OrderEvaluate orderEvaluate = orderEvaluateMapper.selectOne(new QueryWrapper<OrderEvaluate>().eq("id", orderId).eq("courier_id", userId));
            if (ObjectUtils.isEmpty(orderEvaluate)){
                return "1";
            }
            return ("").equals(orderEvaluate.getCourierEvaluate())?"1":"0";
        }else if (roleEnum != SysRoleEnum.ADMIN){
            // 普通用户
            OrderEvaluate orderEvaluate = orderEvaluateMapper.selectOne(new QueryWrapper<OrderEvaluate>().eq("id", orderId).eq("user_id", userId));
            if (ObjectUtils.isEmpty(orderEvaluate)){
                return "1";
            }
            return ("").equals(orderEvaluate.getUserEvaluate())?"1":"0";
        }
        return "0";
    }

    /**
     * 查询支付状态
     */
    private OrderPayment getPaymenyById(String orderId){
        return orderPaymentMapper.selectById(orderId);
    }

    // todo 配送员查询订单列表
    // todo 管理员查询订单列表
    // todo 根据id获取快递公司名字（远程调用）
}

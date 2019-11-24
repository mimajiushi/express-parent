package com.cwj.express.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.express.api.order.OrderControllerApi;
import com.cwj.express.common.config.auth.AuthorizeConfig;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.common.enums.OrderStatusEnum;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.order.feignclient.ucenter.UcenterFeignClient;
import com.cwj.express.order.service.OrderEvaluateService;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.order.service.OrderPaymentService;
import com.cwj.express.order.service.RedisService;
import com.cwj.express.utils.ExpressOauth2Util;
import com.cwj.express.vo.order.OrderDashboardVO;
import com.cwj.express.vo.order.OrderDetailVO;
import com.cwj.express.vo.order.OrderHistoryVO;
import com.cwj.express.vo.order.OrderInfoVO;
import com.cwj.express.vo.table.BootstrapTableVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController extends BaseController implements OrderControllerApi {

    private final OrderEvaluateService orderEvaluateService;
    private final OrderInfoService orderInfoService;
    private final OrderPaymentService orderPaymentService;
    private final UcenterFeignClient ucenterFeignClient;
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisService redisService;

//    @Override
//    @GetMapping("/countEvaluate/{id}/{roleId}")
//    public int countEvaluate(@PathVariable String id, @PathVariable Integer roleId) {
//        return orderEvaluateService.countEvaluate(id, roleId);
//    }

    @Override
    @GetMapping("/userDashboardData")
    @PreAuthorize(AuthorizeConfig.ALL_PAY_USER)
    public OrderDashboardVO getUserDashboardData() {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        return orderInfoService.getUserDashboardData(id.getId());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_COURIER')")
    @GetMapping("/courierDashboardData")
    public OrderDashboardVO getCourerDashboardData() {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        return orderInfoService.getCourerDashboardData(id.getId());
    }

    @Override
    @PostMapping("/createOrder")
    @PreAuthorize(AuthorizeConfig.ALL_PAY_USER)
    public ResponseResult createOrder(@Valid OrderInfoVO orderInfoVO) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser sysUser = ucenterFeignClient.getById(id.getId());

        String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + sysUser.getSchoolId();
        Long count = redisService.zcard(key);
        if (count == 0){
            return ResponseResult.FAIL(CommonCode.COURIER_NOT_EXIST);
        }
        return orderInfoService.createOrder(orderInfoVO, sysUser.getId());
    }

    @Override
    @PostMapping("/getOrder/{orderId}")
    public OrderInfo getOrderById(@PathVariable String orderId) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        String userId = id.getId();
        return orderInfoService.getOrderByIdAndUserId(orderId, userId);
    }

    @Override
    @PostMapping("/getPayment/{orderId}")
    public OrderPayment getPaymentById(@PathVariable String orderId) {
        return orderPaymentService.getByOrderId(orderId);
    }

    @Override
    @GetMapping("/orderList")
    public BootstrapTableVO<OrderHistoryVO> orderList(@RequestParam(defaultValue = "1", required = false) Integer current,
                                                      @RequestParam(defaultValue = "10", required = false) Integer size,
                                                      OrderHistoryVO orderHistoryVO) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser sysUser = ucenterFeignClient.getById(id.getId());
        Page<OrderInfo> page = new Page<>(current, size);
        return orderInfoService.orderList(page, sysUser.getId(), sysUser.getRole(), orderHistoryVO);
    }

    @Override
    @GetMapping("/orderDetail/{orderId}")
    public ResponseResult orderDetail(@PathVariable String orderId) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser sysUser = ucenterFeignClient.getById(id.getId());
        OrderDetailVO orderDetailVO = orderInfoService.orderDetail(orderId, sysUser.getId(), sysUser.getRole());
        if (ObjectUtils.isEmpty(orderDetailVO)){
            return ResponseResult.FAIL(CommonCode.ORDER_NOT_EXIST_ERROR);
        }
        return ResponseResult.SUCCESS(orderDetailVO);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_COURIER')")
    @PostMapping("/pickUpOrder/{orderId}")
    public ResponseResult pickUpOrder(@PathVariable String orderId, String courierRemark) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        boolean success = orderInfoService.pickUpOrder(orderId, id.getId(), courierRemark);
        if (!success){
            return ResponseResult.FAIL(CommonCode.ORDER_HAS_BEEN_CHANGEED);
        }
        return ResponseResult.SUCCESS();
    }

    @Override
    @PostMapping("/finishOrder/{orderId}")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult finishOrder(@PathVariable String orderId, String courierRemark) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        boolean success = orderInfoService.finishOrder(orderId, id.getId(), courierRemark);
        if (!success){
            return ResponseResult.FAIL(CommonCode.ORDER_HAS_BEEN_CHANGEED);
        }
        return ResponseResult.SUCCESS();
    }

    @Override
    @PostMapping("/setOrderException/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_COURIER','ROLE_ADMIN')")
    public ResponseResult setOrderException(@PathVariable String orderId, String courierRemark) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser sysUser = ucenterFeignClient.getById(id.getId());
        boolean success = false;
        if (SysRoleEnum.ADMIN == sysUser.getRole()){
            OrderInfo orderInfo = orderInfoService.getOrderById(orderId);
            success = orderInfoService.setOrderExcetion(orderId, orderInfo.getCourierId(), courierRemark);
        }else {
            success = orderInfoService.setOrderExcetion(orderId, sysUser.getId(), courierRemark);
        }
        if (!success){
            return ResponseResult.FAIL(CommonCode.ORDER_HAS_BEEN_CHANGEED);
        }
        return ResponseResult.SUCCESS();
    }

    @Override
    @GetMapping("/countCourierScore/{courierId}")
    @PreAuthorize("hasAnyRole('ROLE_COURIER','ROLE_ADMIN')")
    public Double countCourierScore(@PathVariable(required = false) String courierId) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser sysUser = ucenterFeignClient.getById(id.getId());
        if (SysRoleEnum.ADMIN == sysUser.getRole()){
            return orderInfoService.countCourierScore(courierId);
        }
        return orderInfoService.countCourierScore(id.getId());
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_COURIER','ROLE_ADMIN')")
    @PostMapping("/reDistributionCourier")
    public ResponseResult reDistributionCourier(String[] orderIds) {
        int count = 0;
        // 校验订单是否都能更新
        List<OrderInfo> orderInfos = orderInfoService.getOrderByIdAndStatus(orderIds, OrderStatusEnum.WAIT_PICK_UP.getStatus(), OrderStatusEnum.TRANSPORT.getStatus());
        if (orderIds.length != orderInfos.size()){
            return ResponseResult.FAIL(CommonCode.ORDER_COUNT_NOT_EQ);
        }
        for (OrderInfo orderInfo : orderInfos) {
            SysUser courier = ucenterFeignClient.getById(orderInfo.getCourierId());
            TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(
                    RocketmqConfig.DISTRIBUTION_COURIER_GROUP,
                    RocketmqConfig.DISTRIBUTION_COURIER_TOPIC,
                    MessageBuilder.withPayload(orderInfo.getId() + "@@re")
                            .setHeader("type", "re")
                            .setHeader("orderId", orderInfo.getId())
                            // userId用于后期清除缓存的
                            .setHeader("userId", orderInfo.getUserId())
                            .setHeader("courierId", courier.getId())
                            .setHeader("schoolId", courier.getSchoolId()).build(),
                    null
            );
            LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState();
            if (localTransactionState == LocalTransactionState.COMMIT_MESSAGE){
                count++;
            }
        }
        return ResponseResult.SUCCESS("重新分配成功！分配订单数：" + count);
    }

}

package com.cwj.express.order.controller;

import com.cwj.express.api.order.OrderControllerApi;
import com.cwj.express.common.config.auth.AuthorizeConfig;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.order.service.OrderEvaluateService;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.order.service.OrderPaymentService;
import com.cwj.express.utils.ExpressOauth2Util;
import com.cwj.express.vo.order.OrderDashboardVO;
import com.cwj.express.vo.order.OrderInfoVO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PreAuthorize(AuthorizeConfig.ALL_PAY_USER)
public class OrderController extends BaseController implements OrderControllerApi {

    private final OrderEvaluateService orderEvaluateService;
    private final OrderInfoService orderInfoService;
    private final OrderPaymentService orderPaymentService;

    @Override
    @GetMapping("/countEvaluate/{id}/{roleId}")
    public int countEvaluate(@PathVariable String id, @PathVariable Integer roleId) {
        return orderEvaluateService.countEvaluate(id, roleId);
    }

    @Override
    @GetMapping("/userDashboardData/{userId}")
    public OrderDashboardVO getUserDashboardData(@PathVariable String userId) {
        return orderInfoService.getUserDashboardData(userId);
    }

    @Override
    @PostMapping("/createOrder")
    public ResponseResult createOrder(@Valid OrderInfoVO orderInfoVO) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        String userId = id.getId();
        return orderInfoService.createOrder(orderInfoVO, userId);
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

}

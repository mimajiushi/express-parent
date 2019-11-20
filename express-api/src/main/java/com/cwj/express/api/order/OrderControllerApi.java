package com.cwj.express.api.order;

import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.vo.order.OrderDashboardVO;
import com.cwj.express.vo.order.OrderDetailVO;
import com.cwj.express.vo.order.OrderHistoryVO;
import com.cwj.express.vo.order.OrderInfoVO;
import com.cwj.express.vo.table.BootstrapTableVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Api(value="获取订单信息api",tags = "订单controller")
public interface OrderControllerApi {
//    @ApiOperation("获取用户收到的评价条数（用户分为配送员和下单用户）")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "用户id"),
//            @ApiImplicitParam(name = "roleId", value = "用户角色id")
//    })
//    public int countEvaluate(@PathVariable String id, @PathVariable Integer roleId);

    @ApiOperation("获取下单用户仪表盘的订单信息")
    public OrderDashboardVO getUserDashboardData();

    @ApiOperation("获取配送员仪表盘的订单信息")
    public OrderDashboardVO getCourerDashboardData();

    @ApiOperation("创建订单接口")
    public ResponseResult createOrder(@Valid OrderInfoVO orderInfoVO);

    @ApiOperation("根据订单id和用户id(token获取)查询订单信息")
    public OrderInfo getOrderById(@PathVariable String orderId);

    @ApiOperation("根据订单id获取订单支付状态信息")
    public OrderPayment getPaymentById(@PathVariable String orderId);

    @ApiOperation(value = "获取用户的历史订单记录，角色不同获取到的记录也不同")
    public BootstrapTableVO<OrderHistoryVO> orderList(@RequestParam(defaultValue = "1", required = false) Integer current,
                                                      @RequestParam(defaultValue = "10", required = false) Integer size,
                                                      OrderHistoryVO orderHistoryVO);

    @ApiOperation("根据订单id查询订单详情信息")
    public ResponseResult orderDetail(@PathVariable String orderId);

    @ApiOperation("配送员揽收订单")
    public ResponseResult pickUpOrder(@PathVariable String orderId, String courierRemark);
}

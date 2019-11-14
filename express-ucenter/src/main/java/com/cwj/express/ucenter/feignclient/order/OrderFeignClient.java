package com.cwj.express.ucenter.feignclient.order;

import com.cwj.express.common.constant.ExpressServiceListConstant;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.vo.order.OrderDashboardVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author cwj
 * 订单服务调用
 * 提醒： fallbackFactory可以拿到异常， fallback不可以
 */

@FeignClient(
        name = ExpressServiceListConstant.EXPRESS_ORDER,
        fallbackFactory = OrderFeignClientFallbackFactory.class
)
public interface OrderFeignClient {
    @GetMapping("/order/countEvaluate/{id}/{roleId}")
    public int countEvaluate(@PathVariable("id") String id, @PathVariable("roleId") Integer roleId);

    @GetMapping("/order/userDashboardData/{userId}")
    public OrderDashboardVO getUserDashboardData(@PathVariable("userId") String userId);

    @PostMapping("/order/getOrder/{orderId}")
    public OrderInfo getOrderById(@PathVariable("orderId") String orderId);

    @PostMapping("/order/getPayment/{orderId}")
    public OrderPayment getPaymentById(@PathVariable("orderId") String orderId);
}

package com.cwj.express.ucenter.feignclient.order;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.vo.order.OrderDashboardVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderFeignClientFallbackFactory implements FallbackFactory<OrderFeignClient> {
    @Override
    public OrderFeignClient create(Throwable throwable) {
        return new OrderFeignClient() {
            @Override
            public int countEvaluate(String id, Integer roleId) {
                log.error("获取用户订单被评价数量异常，用户id:{},角色id:{}，异常信息:{}", id, roleId, throwable.getMessage());
                throwable.printStackTrace();
                return -999;
            }

            @Override
            public OrderDashboardVO getUserDashboardData(String userId) {
                log.error("获取用户仪表盘订单信息异常，用户id:{}，异常信息:{}", userId, throwable.getMessage());
                return OrderDashboardVO.builder()
                        .waitPaymentCount(-999)
                        .transportCount(-999)
                        .waitCount(-999).build();
            }

            @Override
            public OrderInfo getOrderById(String orderId) {
                log.error("获取用户订单详情（结算）异常， 订单id:{}，异常信息:{}", orderId, throwable.getMessage());
                return OrderInfo.builder().build();
            }

            @Override
            public OrderPayment getPaymentById(String orderId) {
                log.error("获取订单支付信息异常，订单id:{}，异常信息:{}", orderId, throwable.getMessage());
                return null;
            }
        };
    }

}

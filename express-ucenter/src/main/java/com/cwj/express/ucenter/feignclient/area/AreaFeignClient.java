package com.cwj.express.ucenter.feignclient.area;

import com.cwj.express.common.constant.ExpressServiceListConstant;
import com.cwj.express.domain.area.DataCompany;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.ucenter.feignclient.order.OrderFeignClientFallbackFactory;
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
        name = ExpressServiceListConstant.EXPRESS_AREA,
        fallbackFactory = OrderFeignClientFallbackFactory.class
)
public interface AreaFeignClient {
    @GetMapping("/area/company/{id}")
    public DataCompany getCompanyById(@PathVariable("id") Integer id);
}

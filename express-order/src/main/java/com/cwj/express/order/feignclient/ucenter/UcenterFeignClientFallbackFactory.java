package com.cwj.express.order.feignclient.ucenter;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.vo.order.OrderDashboardVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UcenterFeignClientFallbackFactory implements FallbackFactory<UcenterFeignClient> {
    @Override
    public UcenterFeignClient create(Throwable throwable) {
        return new UcenterFeignClient() {
            @Override
            public SysRolesLevel getRoleMsgByUserId() {
                log.error(throwable.getMessage());
                return SysRolesLevel.builder().roleName("服务器出错").roleDesc("服务器出错").build();
            }
        };
    }

}

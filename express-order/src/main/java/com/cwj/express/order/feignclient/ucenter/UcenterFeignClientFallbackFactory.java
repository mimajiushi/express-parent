package com.cwj.express.order.feignclient.ucenter;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.vo.order.OrderDashboardVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UcenterFeignClientFallbackFactory implements FallbackFactory<UcenterFeignClient> {
    @Override
    public UcenterFeignClient create(Throwable throwable) {
        return new UcenterFeignClient() {
            @Override
            public SysRolesLevel getRoleMsgByUserId() {
                log.error("用户中心远程调用异常！获取用户权限信息失败！异常信息：{}", throwable.getMessage());
                return null;
            }

            @Override
            public List<SysUser> getAllCouriers() {
                log.error("用户中心远程调用异常！获取所有配送员信息失败！异常信息：{}", throwable.getMessage());
                return null;
            }

            @Override
            public SysUser getById(String userId) {
                throwable.printStackTrace();
                log.error("用户中心远程调用异常！根据用户id获取用户信息失败！异常信息：{}", throwable.getMessage());
                return null;
            }
        };

    }
}

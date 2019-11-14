package com.cwj.express.order.feignclient.ucenter;

import com.cwj.express.common.constant.ExpressServiceListConstant;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author cwj
 * 用户中心服务调用
 * 提醒： fallbackFactory可以拿到异常， fallback不可以
 */

@FeignClient(
        name = ExpressServiceListConstant.EXPRESS_UCENTER,
        fallbackFactory = UcenterFeignClientFallbackFactory.class
)
public interface UcenterFeignClient {
    @PostMapping("/ucenter/getRoleMsgByUserId")
    public SysRolesLevel getRoleMsgByUserId();

}

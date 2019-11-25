package com.cwj.express.order.init;


import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.order.feignclient.ucenter.UcenterFeignClient;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.order.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InitCourierScoreTask {

    private final UcenterFeignClient ucenterFeignClient;
    private final OrderInfoService orderInfoService;
    private final RedisService redisService;

    /**
     * 初始化配送员分数
     */
    @PostConstruct
    private void init() {
        log.info("！！！    请注意要先启动 express-ucenter 服务    ！！！");
        List<SysUser> allCouriers = ucenterFeignClient.getAllCouriers();
        if (ObjectUtils.isEmpty(allCouriers)){
            log.error("！！！    没有配送员/用户中心未启动    ！！！");
        }
        for (SysUser courier : allCouriers) {
            Double socre = orderInfoService.countCourierScore(courier.getId());
            String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + courier.getSchoolId();
            redisService.zadd(key, courier.getId(), socre);
        }
    }
}

package com.cwj.express.order.controller;

import com.cwj.express.api.order.OrderControllerApi;
import com.cwj.express.common.config.auth.AuthorizeConfig;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController extends BaseController {
    private final DefaultRedisScript<String> redisScript;
    private final StringRedisTemplate stringRedisTemplate;


    /**
     * 测试lua脚本
     * 记得先初始化一些数据
     */
    @GetMapping("/luaTest")
    public String test(){
        List<String> keys = Arrays.asList("qid1", "orderid");
        String res = stringRedisTemplate.execute(redisScript, keys, "600");
        return res;
    }
}

package com.cwj.express.ucenter.controller;

import com.cwj.express.api.ucenter.UcenterControllerApi;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.ucenter.service.RedisService;
import com.cwj.express.ucenter.service.SysRolesLevelService;
import com.cwj.express.ucenter.service.SysUserService;
import com.cwj.express.utils.ExpressOauth2Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/ucenter")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class UcenterController extends BaseController implements UcenterControllerApi {

    private final SysRolesLevelService sysRolesLevelService;
    private final SysUserService sysUserService;
    private final RedisService redisService;

//    @Value("${rocketmq.producer.send-message-timeout}")
//    private Long timeout;

    /**
     * 用于测试
     */
//    private final RocketMQTemplate rocketMQTemplate;
//    @PreAuthorize("hasAnyRole('SVIP_USER')")
    @GetMapping("/hello")
    public String hello(){
        Double zscore = redisService.zscore("qid1", "asdas");
        return String.valueOf(zscore);
//        rocketMQTemplate.syncSend(
//                "testTopic",
//                MessageBuilder.withPayload("你好payload").build(),
//                timeout,
//                MessageDelayLevel.TIME_1S.level
//        );
//        return "hello";
    }

    @PostMapping("/getRoleMsgByUserId")
    @Override
    public SysRolesLevel getRoleMsgByUserId() {
        SysUser id;
         id = ExpressOauth2Util.getUserJwtFromAttribute(request);
         if (ObjectUtils.isEmpty(id)){
             id = ExpressOauth2Util.getUserJwtFromHeader(request);
         }
         if (ObjectUtils.isEmpty(id)){
             ExceptionCast.cast(CommonCode.AUTH_FALL);
         }

         return sysRolesLevelService.getByUserId(id.getId());
    }

    @Override
    @GetMapping("/getAllCouriers")
    public List<SysUser> getAllCouriers() {
        return sysUserService.getAllCouriers();
    }

    @Override
    @GetMapping("/getById/{userId}")
    public SysUser getById(@PathVariable String userId) {
        return sysUserService.getById(userId);
    }

    @Override
    @PostMapping
    public ResponseResult courierLeave(String reason) {
        // todo 编写业务层
        return null;
    }

}

package com.cwj.express.ucenter.controller;

import com.cwj.express.api.ucenter.UcenterControllerApi;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.ucenter.dao.UserEvaluateMapper;
import com.cwj.express.ucenter.service.*;
import com.cwj.express.utils.CookieUtil;
import com.cwj.express.utils.ExpressOauth2Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
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
    private final UserEvaluateService userEvaluateService;
    private final CourierSignService courierSignService;

//    @Value("${rocketmq.producer.send-message-timeout}")
//    private Long timeout;

    /**
     * 用于测试
     */
//    private final RocketMQTemplate rocketMQTemplate;
//    @PreAuthorize("hasAnyRole('SVIP_USER')")
    @GetMapping("/hello")
    public String hello(){
//        Double zscore = redisService.zscore("qid1", "asdas");
//        Long zrem = redisService.zrem("qid1", "cid1");// 返回值变更成功的数量
//        return String.valueOf(zrem);
//        rocketMQTemplate.syncSend(
//                "testTopic",
//                MessageBuilder.withPayload("你好payload").build(),
//                timeout,
//                MessageDelayLevel.TIME_1S.level
//        );
        userEvaluateService.updateScoreAndCount("1", new BigDecimal(5));
        return "hello";
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
    @PostMapping("/courierLeave")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult courierLeave(String reason) {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser sysUser = sysUserService.getById(id.getId());
        sysUserService.courierLeave(sysUser, reason);
        return ResponseResult.SUCCESS();
    }

    @Override
    @PostMapping("/courierReWork")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult courierReWork() {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser sysUser = sysUserService.getById(id.getId());
        sysUserService.courierReWork(sysUser);
        return ResponseResult.SUCCESS();
    }

    @Override
    @PostMapping("/courierSignNormal")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult courierSignNormal() {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser courier = sysUserService.getById(id.getId());
        if (sysUserService.isLeave(courier)){
            return ResponseResult.FAIL(CommonCode.LEAVE_STATUS_CAN_NOT_SIGN);
        }
        return courierSignService.courierSignNormal(courier.getId());
    }

    @Override
    @PostMapping("/courierSignOT")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult courierSignOT() {
        SysUser id = ExpressOauth2Util.getUserJwtFromHeader(request);
        SysUser courier = sysUserService.getById(id.getId());
        if (sysUserService.isLeave(courier)){
            return ResponseResult.FAIL(CommonCode.LEAVE_STATUS_CAN_NOT_SIGN);
        }
        return courierSignService.courierSignOT(id.getId());
    }

    @Override
    @PostMapping("/userLogout")
    public ResponseResult userLogout() {
        try {
            clearTokenFromCookie();
        }catch (Exception e){
            return ResponseResult.FAIL();
        }
        return ResponseResult.SUCCESS();
    }

    /**
     * 从cookie删除token
     */
    private void clearTokenFromCookie(){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response,"localhost","/","uid","",0,false);
    }





}

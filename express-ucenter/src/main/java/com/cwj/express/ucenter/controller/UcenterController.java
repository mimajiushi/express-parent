package com.cwj.express.ucenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.express.api.ucenter.UcenterControllerApi;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.area.DataSchool;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.ucenter.dao.UserEvaluateMapper;
import com.cwj.express.ucenter.feignclient.area.AreaFeignClient;
import com.cwj.express.ucenter.service.*;
import com.cwj.express.utils.CookieUtil;
import com.cwj.express.utils.ExpressOauth2Util;
import com.cwj.express.utils.LocalDateTimeUtils;
import com.cwj.express.vo.table.BootstrapTableVO;
import com.cwj.express.vo.ucenter.UserInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/ucenter")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class UcenterController extends BaseController implements UcenterControllerApi {

    private final SysRolesLevelService sysRolesLevelService;
    private final SysUserService sysUserService;
    private final RedisService redisService;
    private final UserEvaluateService userEvaluateService;
    private final CourierSignService courierSignService;
    private final AreaFeignClient areaFeignClient;

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

    @Override
    @GetMapping("/userInfoList")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BootstrapTableVO<UserInfoVo> userListByParam(@RequestParam(required = false, defaultValue = "1") Integer current,
                                            @RequestParam(required = false, defaultValue = "10") Integer size,
                                            UserInfoVo userInfoVo) {
        // 根据状态或角色查询
        Page<SysUser> page = new Page<>(current, size);
        return sysUserService.listByParam(page, userInfoVo);


    }

    @Override
    @GetMapping("/userInfoDetail/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult userInfoDetail(@PathVariable String userId,
                                         UserInfoVo userInfoVo) {
        SysUser sysUser = sysUserService.getById(userId);

        UserInfoVo userInfoVo1 = UserInfoVo.builder()
                .id(sysUser.getId())
                .sex(sysUser.getSex().getCname())
                .idCard(sysUser.getIdCard())
                .workerIdCard(sysUser.getStudentIdCard())
                .realName(sysUser.getRealName()).build();
        String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + sysUser.getSchoolId();
        Double score = redisService.zscore(key, sysUser.getId());
        // 获取学校名字
        DataSchool school = areaFeignClient.getSchoolInfoById(String.valueOf(sysUser.getSchoolId()));
        String schoolName = school.getName();

        userInfoVo1.setRoleName(sysUser.getRole().getCnName());
        userInfoVo1.setSchoolName(schoolName);
        if (SysRoleEnum.COURIER == sysUser.getRole()){
            if (ObjectUtils.isEmpty(score)){
                userInfoVo1.setLeaveStatusStr("是");
            }else {
                userInfoVo1.setLeaveStatusStr("否");
            }
        }else {
            userInfoVo1.setLeaveStatusStr("该角色没有请假功能");
        }
        return ResponseResult.SUCCESS(userInfoVo1);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getCourierSignDetail/{courierId}")
    public ResponseResult getCourierSignDetail(@PathVariable String courierId, String startDateStr, String endDateStr){
        Map<String, Long> resMap = new HashMap<>();
        // 根据日期 用户id 查询 配送员各签到情况、请假状态
        SysUser sysUser = sysUserService.getById(courierId);
        LocalDate startDate = sysUser.getCreateDate().toLocalDate().plusDays(-1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        // 日期转换
        if (!StringUtils.isEmpty(startDateStr)){
            startDate = LocalDateTimeUtils.ymdParseToLocalData(startDateStr).plusDays(-1);
        }
        if (!StringUtils.isEmpty(endDateStr)){
            endDate = LocalDateTimeUtils.ymdParseToLocalData(endDateStr).plusDays(1);
        }

        // 日期内工作总天数
        Long workDaysCount = ChronoUnit.DAYS.between(startDate, endDate);
        // 获取在岗天数
        Long normalSignCount = Long.valueOf(courierSignService.getSignCount(sysUser.getId(), 0, startDate, endDate));
        // 获取加班天数
        Long workOTSignCount = Long.valueOf(courierSignService.getSignCount(sysUser.getId(), 1, startDate, endDate));
        // 没有签到的天数
        Long notWorkCount = workDaysCount - normalSignCount;

        resMap.put("normalSignCount", normalSignCount);
        resMap.put("workSignCount", workOTSignCount);
        resMap.put("notWorkCount", notWorkCount);
        return ResponseResult.SUCCESS(resMap);
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

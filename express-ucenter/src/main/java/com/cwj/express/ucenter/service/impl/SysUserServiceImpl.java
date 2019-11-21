package com.cwj.express.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.enums.CourierLeaveStatusEnum;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.domain.ucenter.CourierLeaveLog;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.ucenter.dao.CourierLeaveLogMapper;
import com.cwj.express.ucenter.dao.SysUserMapper;
import com.cwj.express.ucenter.feignclient.order.OrderFeignClient;
import com.cwj.express.ucenter.service.RedisService;
import com.cwj.express.ucenter.service.SysUserService;
import com.cwj.express.utils.LocalDateTimeUtils;
import com.cwj.express.vo.ucenter.UserInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cwj
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final RedisService redisService;
    private final CourierLeaveLogMapper courierLeaveLogMapper;
    private final OrderFeignClient orderFeignClient;

    @Override
    public SysUser getExtByUserName(String username) {
        SysUser sysUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>()
                        .eq("username", username)
        );
        return sysUser;
    }


    @Override
    public SysUser getById(String id) {
        // todo 设置redis缓存
        return sysUserMapper.selectById(id);
    }

    @Override
    public List<SysUser> getAllCouriers() {
        return sysUserMapper.selectList(new QueryWrapper<SysUser>().eq("role_id", SysRoleEnum.COURIER.getType()));
    }

    @Override
    public UserInfoVo getUserInfo(String userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        boolean leave = false;
        // redis取分数，如果为空则说明配送员正在请假
        if (SysRoleEnum.COURIER == sysUser.getRole()){
            String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + sysUser.getSchoolId();
            Double score = redisService.zscore(key, sysUser.getId());
            if (ObjectUtils.isEmpty(score)){
                leave = true;
            }
        }
        return UserInfoVo.builder()
                .roleName(sysUser.getRole().getCnName())
                .username(sysUser.getUsername())
                .realName(sysUser.getRealName())
                .sex(sysUser.getSex().getCname())
                .tel(sysUser.getTel())
                .leave(leave).build();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean courierLeave(SysUser sysUser, String reason) {
        LocalDateTime temoNow = LocalDateTime.now();
        String timeString = LocalDateTimeUtils.formatToYMDHMS(temoNow);
        LocalDateTime now = LocalDateTimeUtils.ymdhmsParseToLocalDataTime(timeString);
        // 记录到请假日志表
        CourierLeaveLog courierLeaveLog = CourierLeaveLog.builder()
                .courierId(sysUser.getId())
                .leaveResaon(reason)
                .leaveStatusEnum(CourierLeaveStatusEnum.LEAVING)
                .createDate(now)
                .updateDate(now).build();
        courierLeaveLogMapper.insert(courierLeaveLog);
        // redis中删除该配送员
        String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + sysUser.getSchoolId();
        Long count = redisService.zrem(key, sysUser.getId());
        // 删除成功则返回，失败则抛出异常，事务回滚
        if (count > 0){
            return true;
        }else {
            ExceptionCast.cast(CommonCode.COURIER_LEAVE_FALL);
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void courierReWork(SysUser sysUser) {
        // 查询配送员正在配送的订单数量，远程调用
        Double score = orderFeignClient.countCourierScore("null");
        if (ObjectUtils.isEmpty(score)){
            ExceptionCast.cast(CommonCode.COURIER_REWORK_FAIL);
        }
        CourierLeaveLog courierLeaveLog = CourierLeaveLog.builder()
                .leaveStatusEnum(CourierLeaveStatusEnum.RETURNED).build();
        QueryWrapper<CourierLeaveLog> queryWrapper = new QueryWrapper<CourierLeaveLog>()
                .eq("courier_id", sysUser.getId())
                .eq("leave_status", CourierLeaveStatusEnum.LEAVING.getStatus());
        // 乐观锁更新
        courierLeaveLogMapper.update(courierLeaveLog, queryWrapper);
        String key = RedisConfig.COURIER_WEIGHT_DATA + "::" + sysUser.getSchoolId();
        Boolean success = redisService.zadd(key, sysUser.getId(), score);
        if (ObjectUtils.isEmpty(success) || !success){
            ExceptionCast.cast(CommonCode.COURIER_REWORK_FAIL);
        }
    }

}

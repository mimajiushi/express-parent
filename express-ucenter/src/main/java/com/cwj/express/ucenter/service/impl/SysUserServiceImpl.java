package com.cwj.express.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.ucenter.dao.SysUserMapper;
import com.cwj.express.ucenter.service.RedisService;
import com.cwj.express.ucenter.service.SysUserService;
import com.cwj.express.vo.ucenter.UserInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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
            if (!ObjectUtils.isEmpty(score)){
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

}

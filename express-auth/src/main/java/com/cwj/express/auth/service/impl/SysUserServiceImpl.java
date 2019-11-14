package com.cwj.express.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.auth.service.SysUserService;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.auth.dao.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @author cwj
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser getExtByUserName(String username) {
        SysUser sysUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>()
                        .eq("username", username)
        );
        return sysUser;
    }

    @Override
    public SysUser getExtByTel(String tel) {
        return sysUserMapper.selectOne(
                new QueryWrapper<SysUser>()
                        .eq("tel", tel)
        );
    }

    @Override
    public SysUser getById(String id) {
        // todo 设置redis缓存
        return sysUserMapper.selectById(id);
    }

    @Override
    public boolean phoneNumExist(String phoneNum) {
        SysUser sysUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>()
                        .eq("tel", phoneNum)
        );
        return !ObjectUtils.isEmpty(sysUser);
    }
}

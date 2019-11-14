package com.cwj.express.ucenter.service.impl;


import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.ucenter.dao.SysRolesLevelMapper;
import com.cwj.express.ucenter.dao.SysUserMapper;
import com.cwj.express.ucenter.service.SysRolesLevelService;
import com.cwj.express.ucenter.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class SysRolesLevelServiceImpl implements SysRolesLevelService {

    private final SysUserMapper sysUserMapper;
    private final SysRolesLevelMapper sysRolesLevelMapper;

    @Override
    public SysRolesLevel getByUserId(String userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        return sysRolesLevelMapper.selectById(sysUser.getRole().getType());
    }
}

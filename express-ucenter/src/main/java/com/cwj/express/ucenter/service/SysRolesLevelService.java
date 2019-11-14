package com.cwj.express.ucenter.service;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.express.domain.ucenter.SysRolesLevel;

/**
 * <p>
 *  角色业务层
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-12
 */
public interface SysRolesLevelService{

    /**
     * 根据用户id获取对应角色信息
     * @param userId 用户id
     * @return 角色信息(主要是折扣)
     */
    public SysRolesLevel getByUserId(String userId);

}

package com.cwj.express.ucenter.service;

import com.cwj.express.domain.ucenter.SysUser;

/**
 * @author chenwenjie
 */
public interface SysUserService {
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户id和角色信息
     */
    public SysUser getExtByUserName(String username);


    /**
     * 根据用户id获取用户西南西
     * @param id 用户id
     * @return 用户信息
     */
    public SysUser getById(String id);



}

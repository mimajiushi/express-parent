package com.cwj.express.auth.service;

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
     * 根据手机号查询用户信息
     * @param tel 电话号码
     * @return 用户信息
     */
    public SysUser getExtByTel(String tel);

    /**
     * 根据用户id获取用户西南西
     * @param id 用户id
     * @return 用户信息
     */
    public SysUser getById(String id);

    /**
     * 判断电话号码是否注册
     * @param phoneNum 手机号
     * @return true：已注册 false：未注册
     */
    public boolean phoneNumExist(String phoneNum);


}

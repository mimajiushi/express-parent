package com.cwj.express.ucenter.service;

import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.vo.ucenter.UserInfoVo;

import java.util.List;

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


    /**
     * 获取全部配送员信息接口
     * @return 配送员信息列表
     */
    public List<SysUser> getAllCouriers();

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    public UserInfoVo getUserInfo(String userId);

    /**
     * 配送员请假接口
     * @param sysUser 用户对象
     * @param reason 请假原因
     * @return true-操作成功 false-操作失败
     */
    public boolean courierLeave(SysUser sysUser, String reason);

    /**
     * 配送员回到岗位接口
     * @param sysUser 用户对象
     * 失败则抛出异常
     */
    public void courierReWork(SysUser sysUser);

    /**
     * 判断配送员是否请假
     * @param courier 配送员用户对象
     */
    public boolean isLeave(SysUser courier);
}

package com.cwj.express.order.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.order.OrderEvaluate;
import com.cwj.express.order.dao.OrderEvaluateMapper;
import com.cwj.express.vo.order.OrderEvaluateVO;

import java.math.BigDecimal;

/**
 * @author cwj
 * 订单评价业务
 */

public interface OrderEvaluateService {

    /**
     * 根据用户id和角色获取被评价tiaoshu
     */
    int countEvaluate(String id, Integer roleId);


    /**
     * 用户评价
     * @param orderId 订单id
     * @param userId 用户id
     * @param score 评分
     * @param evaluate 评价内容
     * @param roleEnum 用户角色枚举
     * @return 操作成功/失败信息
     */
    ResponseResult evaluate(String orderId, String userId, BigDecimal score, String evaluate, SysRoleEnum roleEnum);

    /**
     * 根据id获取评价信心
     * @param orderId 订单id
     * @return 评价信息
     */
    OrderEvaluate getById(String orderId);

    /**
     * 分页获取用户评价信息
     * @param page 分页对象
     * @param userId 用户id
     * @param roleEnum 角色枚举
     * @return com.cwj.express.vo.order.OrderEvaluateVO
     */
    OrderEvaluateVO getPageByUserId(Page<OrderEvaluate> page, String userId, SysRoleEnum roleEnum);
}

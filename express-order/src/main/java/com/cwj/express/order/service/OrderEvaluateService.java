package com.cwj.express.order.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.domain.order.OrderEvaluate;

/**
 * @author cwj
 * 订单评价业务
 */

public interface OrderEvaluateService {

    /**
     * 根据用户id和角色获取被评价tiaoshu
     */
    int countEvaluate(String id, Integer roleId);

}

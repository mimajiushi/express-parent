package com.cwj.express.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.common.config.redis.RedisConfig;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.domain.order.OrderEvaluate;
import com.cwj.express.order.dao.OrderEvaluateMapper;
import com.cwj.express.order.service.OrderEvaluateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class OrderEvaluateServiceImpl implements OrderEvaluateService {

    private final OrderEvaluateMapper orderEvaluateMapper;

    @Override
    @Cacheable(cacheNames = RedisConfig.COUNT_EVALUATE_DATA, key = "#id")
    public int countEvaluate(String id, Integer roleId) {
        Integer count = 0;
        if (SysRoleEnum.USER.getType() == roleId){
            count = orderEvaluateMapper.selectCount(
                    new QueryWrapper<OrderEvaluate>().eq("user_id", id).ne("courier_evaluate", ""));
        }else if (SysRoleEnum.COURIER.getType() == roleId){
            count = orderEvaluateMapper.selectCount(
                    new QueryWrapper<OrderEvaluate>().eq("courier_id", id).ne("user_evaluate", ""));
        }
        return count;
    }
}

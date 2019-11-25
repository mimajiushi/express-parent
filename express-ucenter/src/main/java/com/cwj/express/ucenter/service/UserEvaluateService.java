package com.cwj.express.ucenter.service;

import com.cwj.express.domain.ucenter.UserEvaluate;

import java.math.BigDecimal;

/**
 * @author cwj
 * 用户（被）评分信息
 */
public interface UserEvaluateService {

    /**
     * 根据用户id获取评分信息
     * @param id 用户id
     * @return 评分信息
     */
    public UserEvaluate getScoreById(String id);

    /**
     * 更新用户分数和被评价数量
     * @param userId 用户id
     * @param score 分数
     */
    public void updateScoreAndCount(String userId, BigDecimal score);
}

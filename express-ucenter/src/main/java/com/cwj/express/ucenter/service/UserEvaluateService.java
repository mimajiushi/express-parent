package com.cwj.express.ucenter.service;

import com.cwj.express.domain.ucenter.UserEvaluate;

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
}

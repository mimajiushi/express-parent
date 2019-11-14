package com.cwj.express.ucenter.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.express.domain.ucenter.UserFeedback;
import com.cwj.express.vo.ucenter.UserFeedbackVO;

/**
 * <p>
 * 用户反馈业务
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-02
 */
public interface UserFeedbackService{
    /**
     * 获取仪表盘用户部分反馈数量信息
     * @param userId 用户id
     * @return 用户反馈数量信息
     */
    public UserFeedbackVO getUserDashboardData(String userId);
}

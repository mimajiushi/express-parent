package com.cwj.express.ucenter.service;

import com.cwj.express.common.enums.FeedbackStatusEnum;
import com.cwj.express.vo.ucenter.UserFeedbackVO;

import java.time.LocalDate;

/**
 * <p>
 * 用户反馈业务
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-26
 */
public interface UserFeedbackService{
    /**
     * 获取仪表盘用户部分反馈数量信息
     * @param userId 用户id
     * @return 用户反馈数量信息
     */
    public UserFeedbackVO getUserDashboardData(String userId);

    /**
     * 根据日期查找未处理的反馈数量
     * @param feedbackStatusEnum 类型枚举
     * @param start 开始日期
     * @param end 结束日期
     */
    public Integer getCountByStatusAndDate(FeedbackStatusEnum feedbackStatusEnum, LocalDate start, LocalDate end);

}

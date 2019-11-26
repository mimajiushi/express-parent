package com.cwj.express.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cwj.express.common.enums.FeedbackStatusEnum;
import com.cwj.express.domain.ucenter.UserFeedback;
import com.cwj.express.ucenter.dao.UserFeedbackMapper;
import com.cwj.express.ucenter.service.UserFeedbackService;
import com.cwj.express.vo.ucenter.UserFeedbackVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

/**
 * @author chenwenjie
 * @since 2019-11-02
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class UserFeedbackServiceImpl implements UserFeedbackService {

    private final UserFeedbackMapper userFeedbackMapper;

    @Override
    public UserFeedbackVO getUserDashboardData(String userId) {
        int waitCount = userFeedbackMapper.selectCount(new QueryWrapper<UserFeedback>().
                eq("status", FeedbackStatusEnum.WAIT).
                eq("user_id", userId));
        int processCount = userFeedbackMapper.selectCount(new QueryWrapper<UserFeedback>().
                eq("status", FeedbackStatusEnum.PROCESS).
                eq("user_id", userId));

        return UserFeedbackVO.builder()
                .waitCount(waitCount)
                .processCount(processCount).build();
    }

    @Override
    public Integer getCountByStatusAndDate(FeedbackStatusEnum feedbackStatusEnum, LocalDate start, LocalDate end) {
        QueryWrapper<UserFeedback> userFeedbackQueryWrapper = new QueryWrapper<UserFeedback>()
                .eq("status", feedbackStatusEnum.getStatus())
                .orderByDesc("create_date");
        if (!ObjectUtils.isEmpty(start)){
            start = start.plusDays(-1);
            userFeedbackQueryWrapper.gt("update_date", start);
        }
        if (!ObjectUtils.isEmpty(end)){
            end = end.plusDays(1);
            userFeedbackQueryWrapper.lt("update_date", end);
        }
        return userFeedbackMapper.selectCount(userFeedbackQueryWrapper);
    }
}

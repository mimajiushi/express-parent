package com.cwj.express.ucenter.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.express.domain.ucenter.UserEvaluate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * <p>
 * 用户评分表 Mapper 接口
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-02
 */
public interface UserEvaluateMapper extends BaseMapper<UserEvaluate> {
    @Update("update user_evaluate set score = (score + #{score}) where user_id = #{userId}")
    public void updateScore(@Param("userId") String userId, @Param("score") BigDecimal score);

    @Update("update user_evaluate set count = count + 1 where user_id = #{userId}")
    public void updateCount(@Param("userId") String userId);
}

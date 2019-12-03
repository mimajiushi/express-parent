package com.cwj.express.domain.ucenter;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户评分消息日志
 * </p>
 * 用户处理rocketmq可能重复发送消息
 *
 * @author chenwenjie
 * @since 2019-12-03
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvaluateEventLog extends Model<UserEvaluateEventLog> {

    private static final long serialVersionUID=1L;

    /**
     * 订单id
     */
    @TableId(type = IdType.INPUT)
    private String logId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户评分
     */
    private BigDecimal score;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;


    @Override
    protected Serializable pkVal() {
        return this.logId;
    }

}

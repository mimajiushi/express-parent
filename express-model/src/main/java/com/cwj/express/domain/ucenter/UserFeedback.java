package com.cwj.express.domain.ucenter;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.cwj.express.common.enums.FeedbackStatusEnum;
import com.cwj.express.common.enums.FeedbackTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户反馈表
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-02
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@ApiModel(value="UserFeedback对象", description="用户反馈表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedback extends Model<UserFeedback> {

    @TableId(type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "反馈类型")
    private FeedbackTypeEnum feedbackType;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "反馈内容")
    private String content;

    @ApiModelProperty(value = "反馈状态")
    private FeedbackStatusEnum feedbackStatus;

    @ApiModelProperty(value = "处理人")
    private String handler;

    @ApiModelProperty(value = "处理结果")
    private String result;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Version
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

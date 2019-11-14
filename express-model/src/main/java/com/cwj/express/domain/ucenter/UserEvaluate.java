package com.cwj.express.domain.ucenter;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户评分表
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-02
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@ApiModel(value="UserEvaluate对象", description="用户评分表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvaluate extends Model<UserEvaluate> {

    @ApiModelProperty(value = "用户ID")
    @TableId(type = IdType.INPUT)
    private String userId;

    @ApiModelProperty(value = "用户评分")
    private BigDecimal score;

    @ApiModelProperty(value = "评分基数")
    private Integer count;

    @ApiModelProperty(value = "更新时间")
    @Version
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

}

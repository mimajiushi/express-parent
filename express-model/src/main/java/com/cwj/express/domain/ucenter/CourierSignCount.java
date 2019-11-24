package com.cwj.express.domain.ucenter;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-24
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="CourierSignCount对象", description="")
public class CourierSignCount extends Model<CourierSignCount> {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "配送员id")
    private String courierId;

    @ApiModelProperty(value = "连续签到天数")
    private Integer signCount;

    @ApiModelProperty(value = "0 - 断签记录 1 - 正在连签")
    private Integer signCountType;

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

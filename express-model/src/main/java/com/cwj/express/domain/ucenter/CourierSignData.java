package com.cwj.express.domain.ucenter;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value="CourierSignData对象", description="")
public class CourierSignData extends Model<CourierSignData> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "配送员id")
    private String courierId;

    @ApiModelProperty(value = "签到日期")
    private LocalDateTime signDate;

    @ApiModelProperty(value = "0-日常 1-加班")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Integer signDataType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

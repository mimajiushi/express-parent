package com.cwj.express.domain.ucenter;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.cwj.express.common.enums.CourierLeaveStatusEnum;
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
 * @since 2019-11-21
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="CourierLeaveLog对象", description="")
public class CourierLeaveLog extends Model<CourierLeaveLog> {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.INPUT)
    private String courierId;

    @TableField("leave_status")
    @ApiModelProperty(value = "0 - 请假中 1 - 已经回到岗位")
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private CourierLeaveStatusEnum leaveStatusEnum;

    @ApiModelProperty(value = "请假原因")
    private String leaveResaon;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    @Version
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.courierId;
    }

}

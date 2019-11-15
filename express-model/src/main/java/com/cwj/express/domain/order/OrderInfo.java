package com.cwj.express.domain.order;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.cwj.express.common.enums.OrderDeleteEnum;
import com.cwj.express.common.enums.OrderStatusEnum;
import com.cwj.express.common.enums.OrderTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单信息表
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderInfo对象", description="订单信息表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo extends Model<OrderInfo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "快递单号")
    private String odd;

    @ApiModelProperty(value = "快递公司")
    private Integer company;

    @ApiModelProperty(value = "收件人")
    private String recName;

    @ApiModelProperty(value = "收件短信")
    private String recTel;

    @ApiModelProperty(value = "收货地址")
    private String recAddress;

    @ApiModelProperty(value = "快递寄达地址")
    private String address;

    @ApiModelProperty(value = "备注")
    private String remark;


    @ApiModelProperty(value = "配送员ID")
    private String courierId;

    @ApiModelProperty(value = "订单状态")
    @TableField("status")
    private OrderStatusEnum orderStatus;

    @ApiModelProperty(value = "服务类型(0为送件上门， 1为上门取件)")
    @TableField("type")
    private OrderTypeEnum orderTypeEnum;

    @ApiModelProperty(value = "配送员备注")
    private String courierRemark;

    @TableLogic
    private Integer hasDelete;

    @ApiModelProperty(value = "删除原因")
    @TableField("delete_type")
    private OrderDeleteEnum deleteType;

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

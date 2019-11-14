package com.cwj.express.domain.order;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.cwj.express.common.enums.PaymentStatusEnum;
import com.cwj.express.common.enums.PaymentTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单支付表
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderPayment对象", description="订单支付表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayment extends Model<OrderPayment> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(type = IdType.INPUT)
    private String orderId;

    @ApiModelProperty(value = "支付状态")
    @TableField("status")
    private PaymentStatusEnum paymentStatus;

    @ApiModelProperty(value = "支付方式")
    @TableField("type")
    private PaymentTypeEnum paymentType;

    @ApiModelProperty(value = "付款金额")
    private BigDecimal payment;

    @ApiModelProperty(value = "支付流水号")
    private String paymentId;

    @ApiModelProperty(value = "收款方")
    private String seller;

    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Version
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.orderId;
    }

}

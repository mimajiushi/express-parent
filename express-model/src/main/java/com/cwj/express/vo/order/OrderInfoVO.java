package com.cwj.express.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @author cwj
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="订单信息vo", description="信息用于创建订单")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoVO {
    @ApiModelProperty(value = "收件人", required = true)
    @NotBlank(message = "收件人 必填")
    private String recName;

    @ApiModelProperty(value = "收件电话", required = true)
    @NotBlank(message = "收件电话 必填")
    private String recTel;

    @ApiModelProperty(value = "快递单号")
    private String odd;

    @ApiModelProperty(value = "快递公司", required = true)
    private Integer company;

    @ApiModelProperty(value = "服务类型", required = true)
    private Integer type;

    @ApiModelProperty(value = "快递寄达地址", required = true)
    @NotBlank(message = "快递寄达地址 必填")
    private String address;

    @ApiModelProperty(value = "收货地址", required = true)
    @NotBlank(message = "收货地址 必填")
    private String recAddress;

    @ApiModelProperty(value = "备注", required = true)
    @NotBlank(message = "备注 必填")
    private String remark;

    @ApiModelProperty(value = "重量（向上取整）", required = true)
    private Double weight;
}

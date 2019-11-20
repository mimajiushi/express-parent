package com.cwj.express.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author cwj
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="仪表盘订单信息对象", description="包含待支付，待配送，正在配送的订单数量")
public class OrderDashboardVO implements Serializable {
    @ApiModelProperty(value = "待支付的订单数量(付费用户)")
    private int waitPaymentCount;

    @ApiModelProperty(value = "等待配送员配送的订单(付费用户，配送员)")
    private int waitCount;

    @ApiModelProperty(value = "等待配送员揽收的订单(配送员)")
    private int waitPickUpCount;

    @ApiModelProperty(value = "正在配送的订单（付费用户，配送员）")
    private int transportCount;
}

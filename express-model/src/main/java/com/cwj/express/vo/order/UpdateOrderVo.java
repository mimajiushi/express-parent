package com.cwj.express.vo.order;

import com.cwj.express.common.enums.OrderStatusEnum;
import com.cwj.express.common.enums.PaymentStatusEnum;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author cwj
 * 更新订单、订单支付信息对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderVo {
    /**
     * 订单号
     */
    private String orderId;

    /**
     * 支付宝返回的支付订单号(流水号)
     */
    private String trade_no;

    /**
     * 支付状态
     */
    private PaymentStatusEnum paymentStatusEnum;

    /**
     * 订单状态
     */
    private OrderStatusEnum orderStatusEnum;
}

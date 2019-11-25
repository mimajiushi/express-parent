package com.cwj.express.vo.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单详情vo，宿舍代拿则写在备注
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailVO implements Serializable {
    /**
     * 订单id
     */
    private String orderId = "";

    /**
     * 服务类型
     */
    private String serverType = "";

    /**
     * 学校
     */
    private String schoolName = "";

    /**
     * 收件人名字
     */
    private String recName = "";

    /**
     * 收件人电话
     */
    private String recTel = "";

    /**
     * 收件人地址
     */
    private String recAddress = "";

    /**
     * 寄件人姓名
     */
    private String senderName = "";

    /**
     * 寄件人电话
     */
    private String senderTel = "";

    /**
     * 寄件发起地址
     */
    private String senderAddress = "";

    /**
     * 快递单号
     */
    private String odd = "";

    /**
     * 快递公司
     */
    private String companyName = "";

    /**
     * 用户备注
     */
    private String userRemark = "";

    /**
     * 支付方式
     */
    private String paymentType = "";

    /**
     * 价格
     */
    private String payment = "";

    /**
     * 支付状态
     */
    private String paymentStatus = "";

    /**
     * 订单状态(感觉不是很需要)，因为表格显示了
     */
    private String orderStatus = "";

    /**
     * 配送员名字
     */
    private String courierName = "";

    /**
     * 配送员电话
     */
    private String courierTel = "";

    /**
     * 配送员备注
     */
    private String courierRemark = "";

//    private Integer canScore = 0;


}

package com.cwj.express.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单历史记录VO
 */
@Data
@NoArgsConstructor
public class OrderHistoryVO implements Serializable {
    /**
     * 订单号
     */
    private String id;
    /**
     * 快递单号
     */
    private String odd;
    /**
     * 快递公司
     */
    private String company;

    /**
     * 服务类型
     */
    private String serverType;

    /**
     * 收件人
     */
    private String recName;
    /**
     * 收件电话
     */
    private String recTel;
    /**
     * 快递寄达地址
     */
    private String address;
    /**
     * 收货地址
     */
    private String recAddress;
    /**
     * 备注
     */
    private String remark;

    /**
     * 支付状态
     */
    private Integer paymentStatus;
    /**
     * 支付金额
     */
    private String payment;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 删除原因
     */
    private Integer deleteType;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    /**
     * 能否评分，1可以，0可以
     */
    private String canScore;
}

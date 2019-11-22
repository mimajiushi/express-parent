package com.cwj.express.order.service;

/**
 * @author cwj
 * 分配配送员业务
 */

public interface DistributionCourierService {
    /**
     * 分配配送员
     * @param orderId 订单id
     * @param type 执行类型 first-支付之后分配 re-手动重新分配
     */
    public void distributionCourier(String orderId, String type);
}

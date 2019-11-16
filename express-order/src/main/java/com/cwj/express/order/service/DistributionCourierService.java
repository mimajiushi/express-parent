package com.cwj.express.order.service;

/**
 * @author cwj
 * 分配配送员业务
 */

public interface DistributionCourierService {
    /**
     * 分配配送员
     * @param orderId 订单id
     */
    public void distributionCourier(String orderId);
}

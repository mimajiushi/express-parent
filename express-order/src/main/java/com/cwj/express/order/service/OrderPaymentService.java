package com.cwj.express.order.service;

import com.alipay.api.AlipayApiException;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.vo.order.UpdateOrderVo;

import java.util.Map;

/**
 * 订单支付状态表
 */
public interface OrderPaymentService {


    /**
     * 根据订单id获取支付相关信息
     * @param orderId 订单id
     * @return 支付信息
     */
    public OrderPayment getByOrderId(String orderId);

    /**
     * 校验订单（支付宝回调用）
     * @param params 订单参数
     * @return true - 校验通过    false - 校验失败
     */
    public boolean validAlipay(Map<String,String> params) throws AlipayApiException;

    /**
     * 更新订单支付状态
     * @param updateOrderVo 订单等信息
     * @param userId 用户id（清除缓存）
     */
    public void updatePayment(UpdateOrderVo updateOrderVo, String userId);
}

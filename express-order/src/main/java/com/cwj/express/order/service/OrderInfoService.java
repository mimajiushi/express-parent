package com.cwj.express.order.service;

import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.vo.order.OrderDashboardVO;
import com.cwj.express.vo.order.OrderInfoVO;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author cwj
 * 订单评价业务
 */

public interface OrderInfoService {

    /**
     * 获取付费用户的部分订单信息
     * @param userId 用户id
     * @return 仪表盘订单数量信息
     */
     OrderDashboardVO getUserDashboardData(String userId);

    /**
     * 创建订单并发送取消订单延时消息
     * @param orderInfoVO 订单信息
     * @return 成功则返回订单id， 失败则返回操作失败
     */
    ResponseResult createOrder(OrderInfoVO orderInfoVO, String userId);

    /**
     * 根据订单id和用户id获取订单信息
     * @param orderId 订单信息
     * @param userId 用户id
     * @return 订单信息
     */
    OrderInfo getOrderByIdAndUserId(String orderId, String userId);

    /**
     * 取消订单
     * @param orderId 订单id
     * @param userId 用户id，用于清楚缓存
     * @param timeVersion 订单等信息创建时间，用于乐观锁
     */
    void cancelOrder(String orderId, String userId, LocalDateTime timeVersion);

    /**
     * 更新订单
     * @param orderInfo 订单信息，订单id和用户id不能为空
     */
    void updateOrder(OrderInfo orderInfo);

}

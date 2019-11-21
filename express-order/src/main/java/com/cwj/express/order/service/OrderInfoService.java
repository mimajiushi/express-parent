package com.cwj.express.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.express.common.enums.SysRoleEnum;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.vo.order.OrderDashboardVO;
import com.cwj.express.vo.order.OrderDetailVO;
import com.cwj.express.vo.order.OrderHistoryVO;
import com.cwj.express.vo.order.OrderInfoVO;
import com.cwj.express.vo.table.BootstrapTableVO;

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
     * 获取配送员的仪表盘订单信息
     * @param courierId 配送员id
     * @return 配送员仪表盘订单星系
     */
    OrderDashboardVO getCourerDashboardData(String courierId);

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
     * 获取订单列表
     * @param page 分页信息
     * @param orderHistoryVO 查询条件
     * @param roleEnum 权限信息
     * @param userId 用户id
     * @return 订单信息列表
     */
    BootstrapTableVO<OrderHistoryVO> orderList(Page<OrderInfo> page, String userId, SysRoleEnum roleEnum, OrderHistoryVO orderHistoryVO);

    /**
     * 根据订单id，用户id，用户权限获取订单详情，
     * 管理员不需要比对userId
     * @param orderId 订单id
     * @param userId 用户id
     * @param roleEnum 用户角色
     * @return 订单详情
     */
    OrderDetailVO orderDetail(String orderId, String userId, SysRoleEnum roleEnum);

    /**
     * 根据订单id，配送员id，将订单由揽收状态改为配送中状态
     * @param orderId 订单id
     * @param courierId 配送员id
     * @return 操作是否成功
     */
    boolean pickUpOrder(String orderId, String courierId, String courierRemark);

    /**
     * 根据订单id，配送员id，将订单由配送中/异常状态改为完成状态
     * @param orderId 订单id
     * @param courierId 配送员id
     * @param courierRemark 配送员备注
     * @return  才做是否成功
     */
    boolean finishOrder(String orderId, String courierId, String courierRemark);

    /**
     * 获取配送员校准分数
     * @param courierId 配送员id
     * @return 分数
     */
    Double countCourierScore(String courierId);
}

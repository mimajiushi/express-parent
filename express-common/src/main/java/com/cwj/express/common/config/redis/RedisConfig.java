package com.cwj.express.common.config.redis;

/**
 * redis相关配置
 */
public class RedisConfig {

    /**
     * 配送员变动分数
     */
    public static final double COURIER_SCORE = 10;

    /**
     * 配送员基准分数
     */
    public static final double COURIER_MAX_SCORE = 10000;

    /**
     * 短信过期时间，单位秒
     */
    public static final long SMS_TIME_OUT = 300;

    /**
     * 创建订单信息缓存，缓存10分钟
     */
    public static final long CREATE_ORDER_TTL = 60*10;

    /**
     * 订单详情信息缓存，缓存1小时(根据实际业务可能缓存更久，订单流程全部走完后，除非删除，否则几乎不变)
     */
    public static final long ORDER_INFO_TTL = 60*60;

    /**
     * 地址缓存过期时间
     */
    public static final long AREA_TTL = 60*60*24;

    /**
     * 分配配送员redis日志有效时间 一周
     */
    public static final long DISTRIBUTION_LOG_TIME_OUT = 60*60*24*7;

    /**
     * 短信存储头
     */
    public static final String SMS_HEAD = "SMS_LOGIN";

    /**
     * 地方名存储头
     */
    public static final String AREA_HEAD = "AREA_DATA";

    /**
     * 快递公司存储头
     */
    public static final String COMPANY_DATA_LIST = "COMPANY_DATA_LIST";
    public static final String COMPANY_DATA = "COMPANY_DATA";

    /**
     * 学校数据存储头
     */
    public static final String SCHOOL_DATA_LIST = "SCHOOL_DATA_LIST";
    public static final String SCHOOL_DATA = "SCHOOL_DATA";

    /**
     * 用户评分数据存储头（指被评分）
     */
    public static final String USER_EVALUATE_DATA = "USER_EVALUATE_DATA";

    /**
     * 订单评价数据存储头
     */
    public static final String COUNT_EVALUATE_DATA = "COUNT_EVALUATE_DATA";

    /**
     * 用户仪表盘订单信息存储头  OrderDashboardVO
     */
    public static final String ORDER_INFO_DASHBOARD_DATA = "ORDER_INFO_DASHBOARD_DATA";

    /**
     * 订单信息存储头（数据库订单信息）
     */
    public static final String ORDER_INFO_DATA = "ORDER_INFO_DATA";

    /**
     * 订单支付信息存储头
     */
    public static final String ORDER_PAYMENT_DATA = "ORDER_PAYMENT_DATA";

    /**
     * 配送员区域权重信息存储头
     */
    public static final String COURIER_WEIGHT_DATA = "COURIER_WEIGHT_DATA";

    /**
     * 分配配送员的订单id存储头，存储方式：存储头:orderId(key) 配送员id(value)
     */
    public static final String ORDER_COURIER_DATA = "ORDER_COURIER_DATA";
}

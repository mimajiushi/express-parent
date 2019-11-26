package com.cwj.express.order.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.cwj.express.api.order.PayControllerApi;
import com.cwj.express.common.config.auth.AuthorizeConfig;
import com.cwj.express.common.config.rocket.RocketmqConfig;
import com.cwj.express.common.enums.OrderStatusEnum;
import com.cwj.express.common.enums.PaymentStatusEnum;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.order.config.alipay.AliPayConfig;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.order.service.OrderPaymentService;
import com.cwj.express.utils.ExpressOauth2Util;
import com.cwj.express.vo.order.UpdateOrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class PayController extends BaseController implements PayControllerApi {

    private final OrderPaymentService orderPaymentService;
    private final OrderInfoService orderInfoService;
    private final AliPayConfig aliPayConfig;
    private final AlipayClient alipayClient;
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 支付宝支付接口
     * 参考文档 & 博客
     *      https://docs.open.alipay.com/api_1/alipay.trade.page.pay
     *      https://www.cnblogs.com/xuzijie/p/9677654.html
     * @param orderId 订单id
     */
    @PreAuthorize(AuthorizeConfig.ALL_PAY_USER)
    @GetMapping("/alipay/pay/{orderId}")
    @Override
    public void alipay(@PathVariable String orderId) throws AlipayApiException, IOException {
        SysUser idAndName = ExpressOauth2Util.getUserJwtFromAttribute(request);
        String userid = idAndName.getId();
        OrderInfo orderInfo = orderInfoService.getOrderByIdAndUserId(orderId, userid);
        OrderPayment payment = orderPaymentService.getByOrderId(orderId);

        if (ObjectUtils.isEmpty(orderInfo)){
            ExceptionCast.cast(CommonCode.ORDER_NOT_EXIST_ERROR);
        }
        if (ObjectUtils.isEmpty(payment)){
            ExceptionCast.cast(CommonCode.PAYMENT_NOT_EXIST_ERROR);
        }

        // 设置请求参数
        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
        // 同步页面跳转url(测试环境用，不保证可达率)
        payRequest.setReturnUrl(aliPayConfig.getReturnUrl());
        // 异步通知url（支付宝保证99.999%的可达率，但是需要内网穿透才能调试）
        payRequest.setNotifyUrl(aliPayConfig.getNotifyUrl());

        // 封装公共参数以外的参数
        Map<String,String> map = new HashMap<>(16);
        // 必选 - 商户订单号
        map.put("out_trade_no", orderId);
        // 必选 - 价格(必须是保留两位小数)
        map.put("total_amount", String.valueOf(payment.getPayment()));
        // 必选 - 订单标题
        map.put("subject", "在线支付");
        // 可选 - 订单描述
        map.put("body", "大学校园快递代取管理系统");
        // 必选 - 销售产品码
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        // 可选 - 允许最晚支付时间差
        map.put("timeout_express","10m");
        // 可选 - 公用回传参数
        map.put("passback_params", URLEncoder.encode(userid, "UTF-8"));

        payRequest.setBizContent(JSON.toJSONString(map));

        // 设置响应
        response.setContentType("text/html;charset=utf-8");

        AlipayTradePagePayResponse alipayTradePagePayResponse = alipayClient.pageExecute(payRequest);
        if (alipayTradePagePayResponse.isSuccess()){
            String res = alipayTradePagePayResponse.getBody();
            response.getWriter().write(res);
        }else {
            ExceptionCast.cast(CommonCode.ALI_PAY_GATEWAY_ERROR);
        }
    }

    /**
     * 特别注意！！！return接口是没有passback_params的，只有notify才有
     */
    @PreAuthorize(AuthorizeConfig.ALL_PAY_USER)
    @Override
    @GetMapping("/alipay/return")
    public void alipayReturn() throws AlipayApiException, IOException {
        SysUser idAndName = ExpressOauth2Util.getUserJwtFromAttribute(request);
        String userId = idAndName.getId();
        Map<String, String> payParams = getPayParams(request);
        String orderId = "", tradeNo = "";
        // 验证订单
        boolean pass = orderPaymentService.validAlipay(payParams);
        if (pass){
            // 获取自定义的订单号
            orderId = payParams.get("out_trade_no");
            // 获取支付宝交易订单号（流水号）
            tradeNo = payParams.get("trade_no");

            UpdateOrderVo updateOrderVo = UpdateOrderVo.builder().
                    orderId(orderId).
                    trade_no(tradeNo).
                    orderStatusEnum(OrderStatusEnum.WAIT_DIST).
                    paymentStatusEnum(PaymentStatusEnum.TRADE_SUCCESS).build();
            TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(
                    RocketmqConfig.DISTRIBUTION_COURIER_GROUP,
                    RocketmqConfig.DISTRIBUTION_COURIER_TOPIC,
                    MessageBuilder.withPayload(orderId + "@@first")
                            .setHeader("type", "first")
                            .setHeader("userId", userId)
                            .setHeader("updateOrderVo", JSON.toJSONString(updateOrderVo)).build(),
                    null
            );
            LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState();
            log.info(String.valueOf(localTransactionState));
//            orderPaymentService.updatePayment(updateOrderVo, userId);
        }else {
            ExceptionCast.cast(CommonCode.ALI_PAY_SIGN_ERROR);
        }
        // 重定向回订单详情页面
        response.sendRedirect("http://localhost:40300/ucenter/paymentPage/" + orderId);
    }

    @PostMapping("/alipay/notify")
    @Override
    public void alipayNotify() throws AlipayApiException, IOException {
        Map<String, String> payParams = getPayParams(request);
        boolean pass = orderPaymentService.validAlipay(payParams);
        if (pass){
            //商户订单号
            String orderId = payParams.get("out_trade_no");
            //支付宝交易号
            String tradeNo = payParams.get("trade_no");
            //交易状态
            String tradeStatus = payParams.get("trade_status");
            // 获取userid
            String userId = payParams.get("passback_params");

            UpdateOrderVo updateOrderVo = UpdateOrderVo.builder().orderId(orderId).trade_no(tradeNo).build();

            switch (tradeStatus) {
                case "WAIT_BUYER_PAY":
                    updateOrderVo.setOrderStatusEnum(OrderStatusEnum.WAIT_DIST);
                    updateOrderVo.setPaymentStatusEnum(PaymentStatusEnum.WAIT_BUYER_PAY);
                    orderPaymentService.updatePayment(updateOrderVo, userId);
                    break;
                /*
                 * 关闭订单
                 * （1)订单已创建，但用户未付款，调用关闭交易接口
                 * （2）付款成功后，订单金额已全部退款【如果没有全部退完，仍是TRADE_SUCCESS状态】
                 */
                case "TRADE_CLOSED":
                    updateOrderVo.setOrderStatusEnum(OrderStatusEnum.CANCEL);
                    updateOrderVo.setPaymentStatusEnum(PaymentStatusEnum.TRADE_CLOSED);
                    orderPaymentService.updatePayment(updateOrderVo, userId);
                    break;
                /*
                 * 订单完成
                 * （1）TRADE_SUCCESS状态代表了充值成功，
                 *      也就是说钱已经进了支付宝（担保交易）或卖家（即时到账）；
                 *      这时候，这笔交易应该还可以进行后续的操作（比如三个月后交易状态自动变成TRADE_FINISHED），
                 *      因为整笔交易还没有关闭掉，也就是说一定还有主动通知过来。而TRADE_FINISHED代表了这笔订单彻底完成了，不会再有任何主动通知过来了。
                 */
                case "TRADE_FINISHED" :
                    updateOrderVo.setPaymentStatusEnum(PaymentStatusEnum.TRADE_FINISHED);
                    orderPaymentService.updatePayment(updateOrderVo, userId);
                    break;
                /*
                 * 订单Success
                 * （1）用户付款成功
                 */
                case "TRADE_SUCCESS" :
                    updateOrderVo.setOrderStatusEnum(OrderStatusEnum.WAIT_DIST);
                    updateOrderVo.setPaymentStatusEnum(PaymentStatusEnum.TRADE_SUCCESS);
                    orderPaymentService.updatePayment(updateOrderVo, userId);
                default:break;
            }
            response.getWriter().write("success");
        }else {
            response.getWriter().write("error");
            ExceptionCast.cast(CommonCode.ALI_PAY_SIGN_ERROR);
        }
    }


    /**
     * 获取支付参数
     * @author jitwxs
     * @since 2018/6/4 16:39
     */
    private Map<String,String> getPayParams(HttpServletRequest request) {
        Map<String,String> params = new HashMap<>(16);
        Map<String,String[]> requestParams = request.getParameterMap();

        requestParams.forEach((k, y)->{
            String[] values = requestParams.get(k);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(k, valueStr);
        });

        return params;
    }
}

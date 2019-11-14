package com.cwj.express.api.order;

import com.alipay.api.AlipayApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Api(value="支付功能相关接口",tags = "支付controller")
public interface PayControllerApi {

    @ApiOperation("支付宝支付")
    public void alipay(@PathVariable String orderId) throws AlipayApiException, IOException;


    @ApiOperation("支付宝同步回调接口(非前后分离接口)")
    public void alipayReturn() throws AlipayApiException, IOException;


    @ApiOperation(value = "支付宝异步回调接口",
    notes = "支付宝服务器异步通知，获取支付宝POST过来反馈信息\n" +
            "该方法无返回值，静默处理\n" +
            "订单的状态已该方法为主，其他的状态修改方法为辅 *\n" +
            "（1）程序执行完后必须打印输出“success”（不包含引号）。\n" +
            "如果商户反馈给支付宝的字符不是success这7个字符，支付宝服务器会不断重发通知，直到超过24小时22分钟。\n" +
            "（2）程序执行完成后，该页面不能执行页面跳转。\n" +
            "如果执行页面跳转，支付宝会收不到success字符，会被支付宝服务器判定为该页面程序运行出现异常，而重发处理结果通知\n" +
            "（3）cookies、session等在此页面会失效，即无法获取这些数据\n" +
            "（4）该方式的调试与运行必须在服务器上，即互联网上能访问 *\n"
    )
    public void alipayNotify() throws AlipayApiException, IOException;

}

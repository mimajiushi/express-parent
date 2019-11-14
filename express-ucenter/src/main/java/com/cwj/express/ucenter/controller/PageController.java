package com.cwj.express.ucenter.controller;
import com.cwj.express.common.config.auth.AuthorizeConfig;
import com.cwj.express.common.constant.URLConstant;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.area.DataCompany;
import com.cwj.express.domain.order.OrderInfo;
import com.cwj.express.domain.order.OrderPayment;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.domain.ucenter.UserEvaluate;
import com.cwj.express.ucenter.feignclient.area.AreaFeignClient;
import com.cwj.express.ucenter.feignclient.order.OrderFeignClient;
import com.cwj.express.ucenter.service.*;
import com.cwj.express.utils.ExpressOauth2Util;
import com.cwj.express.vo.order.OrderDashboardVO;
import com.cwj.express.vo.ucenter.UserFeedbackVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;
import java.io.IOException;
import java.math.BigDecimal;


@Controller
@ApiIgnore
@RequestMapping("/ucenter")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PageController extends BaseController {
    private final SysUserService sysUserService;
    private final UserEvaluateService userEvaluateService;
    private final UserFeedbackService userFeedbackService;
    private final OrderFeignClient orderFeignClient;
    private final RedisService redisService;
    private final AreaFeignClient areaFeignClient;
    private final SysRolesLevelService sysRolesLevelService;

    /**
     * 登陆跳转到此处进行再转发
     */
    @GetMapping("/")
    public void showSuccessPage() throws IOException {
        // 从数据库或redis(推荐中查询用户实时信息)，目前先用mysql
        SysUser sysUser = sysUserService.getById((String) request.getAttribute("user_id"));
        if (ObjectUtils.allNotNull(sysUser)){
            switch (sysUser.getRole()) {
                case DIS_FORMAL:
                    response.sendRedirect("/ucenter/completeInfo");
                    return;
                case USER:
                    response.sendRedirect("/ucenter/dashboard");
                    return;
                case VIP_USER:
                    response.sendRedirect("/ucenter/dashboard");
                    return;
                case SVIP_USER:
                    response.sendRedirect("/ucenter/dashboard");
                    return;
                case ADMIN:
                    response.sendRedirect("/ucenter/admin");
                    return;
                case COURIER:
                    response.sendRedirect("/ucenter/dashboard");
                    return;
                default:
                    response.sendRedirect(URLConstant.LOGIN_PAGE_URL);
            }
        }else {
            response.sendRedirect(URLConstant.LOGIN_PAGE_URL);
        }
    }


    /**
     * 控制台页面
     * 这里不能使用缓存
     */
    @PreAuthorize(AuthorizeConfig.ALL_PAY_USER)
    @GetMapping("/dashboard")
    public String dashboard(ModelMap map){
        SysUser id = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysUser sysUser = sysUserService.getById(id.getId());

        map.put("roleName", sysUser.getRole().getCnName());
        map.put("frontName", sysUser.getUsername());

        UserEvaluate scoreById = userEvaluateService.getScoreById(sysUser.getId());
        BigDecimal score = scoreById == null?new BigDecimal(0):scoreById.getScore();
        int evaluateCount = orderFeignClient.countEvaluate(sysUser.getId(), sysUser.getRole().getType());

        String userDesc = "您共收到：" + evaluateCount + "条评价，您的综合评分为：" + score + "分";
        map.put("evaluateDesc", userDesc);

        OrderDashboardVO userDashboardData = orderFeignClient.getUserDashboardData(sysUser.getId());
        String orderDesc = "未支付订单数：：" + userDashboardData.getWaitPaymentCount() +
                "，等待接单数：：" + userDashboardData.getWaitCount() +
                "，正在派送数：" + userDashboardData.getTransportCount();
        map.put("orderDesc", orderDesc);


        UserFeedbackVO feedbackVO = userFeedbackService.getUserDashboardData(sysUser.getId());
        String feedbackDesc = "正在处理的反馈数："+ feedbackVO.getProcessCount() +
                "，未处理的反馈数：" + feedbackVO.getWaitCount();
        map.put("feedbackDesc", feedbackDesc);

        return "user/dashboard";
    }

    /**
     * 管理员页面示例 todo 目前仅用作权限演示用
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String adminPage(ModelMap map){
        return "user/admintest";
    }


    @PreAuthorize(AuthorizeConfig.ALL_PAY_USER)
    @GetMapping("/orderPage")
    public String orderPage(ModelMap map){
        SysUser sysUser = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysRolesLevel sysRolesLevel = sysRolesLevelService.getByUserId(sysUser.getId());
        map.put("frontName", sysUser.getUsername());
        map.put("roleName", sysRolesLevel.getRoleDesc());
        return "user/order";
    }

    @PreAuthorize(AuthorizeConfig.ALL_PAY_USER)
    @GetMapping("/paymentPage/{orderId}")
    public String paymentPage(@PathVariable String orderId, ModelMap map){

        SysUser usernameAndId = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysRolesLevel sysRolesLevel = sysRolesLevelService.getByUserId(usernameAndId.getId());
        OrderInfo orderInfo = orderFeignClient.getOrderById(orderId);
        OrderPayment payment = orderFeignClient.getPaymentById(orderId);
        if (!ObjectUtils.allNotNull(orderInfo)){
            map.put("errorMsg", "订单不存在");
            return "user/payment";
        }
        if (!ObjectUtils.allNotNull(payment)){
            map.put("errorMsg", "订单支付信息不存在");
            return "user/payment";
        }
        DataCompany company = areaFeignClient.getCompanyById(orderInfo.getCompany());

        map.put("serviceType", orderInfo.getOrderTypeEnum().getDesc());
        map.put("companyName", company.getName());
        map.put("orderInfo", orderInfo);
        map.put("price", payment.getPayment());
        map.put("frontName", usernameAndId.getUsername());
        map.put("roleName", sysRolesLevel.getRoleDesc());
        map.put("payStatus", payment.getPaymentStatus().getName());
        return "user/payment";
    }
}
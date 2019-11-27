package com.cwj.express.ucenter.controller;
import com.cwj.express.common.config.auth.AuthorizeConfig;
import com.cwj.express.common.constant.URLConstant;
import com.cwj.express.common.enums.FeedbackStatusEnum;
import com.cwj.express.common.model.response.ResponseResult;
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
import com.cwj.express.vo.ucenter.UserInfoVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;


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
                    response.sendRedirect("/ucenter/adminDashboard");
                    return;
                case COURIER:
                    response.sendRedirect("/ucenter/courierDashboard");
                    return;
                default:
                    response.sendRedirect(URLConstant.LOGIN_PAGE_URL);
            }
        }else {
            response.sendRedirect(URLConstant.LOGIN_PAGE_URL);
        }
    }


    /**
     * 控制台页面(付费用户)
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
//        int evaluateCount = orderFeignClient.countEvaluate(sysUser.getId(), sysUser.getRole().getType());
        int evaluateCount = scoreById.getCount();

        String userDesc = "您共收到：" + evaluateCount + "条评价，您的综合评分为：" + score + "分";
        map.put("evaluateDesc", userDesc);

        OrderDashboardVO userDashboardData = orderFeignClient.getUserDashboardData();
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

    @PreAuthorize("hasRole('ROLE_COURIER')")
    @GetMapping("/courierDashboard")
    public String courierDashboard(ModelMap map){
        SysUser id = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysUser sysUser = sysUserService.getById(id.getId());

        map.put("roleName", sysUser.getRole().getCnName());
        map.put("frontName", sysUser.getUsername());

        UserEvaluate scoreById = userEvaluateService.getScoreById(sysUser.getId());
        BigDecimal score = scoreById == null?new BigDecimal(0):scoreById.getScore();
        int evaluateCount = scoreById.getCount();

        String userDesc = "您共收到：" + evaluateCount + "条评价，您的综合评分为：" + score + "分";
        map.put("evaluateDesc", userDesc);


        OrderDashboardVO courerDashboardData = orderFeignClient.getCourerDashboardData();
        String orderDesc = "等待揽收数：" + courerDashboardData.getWaitPickUpCount() +
                "，正在配送数：" + courerDashboardData.getTransportCount();
        map.put("orderDesc", orderDesc);

//        Map<String, Integer> data2 = feedbackService.getCourierDashboardData();
//        String feedbackDesc = "今日系统新增反馈数：" + data2.get("today") +
//                "，系统等待处理数：" + data2.get("wait");
        String feedbackDesc = "暂时不写";
        map.put("feedbackDesc", feedbackDesc);

        return "courier/dashboard";
    }

    @GetMapping("/adminDashboard")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminDashboard(ModelMap map){
        // 日 发(完成)/收(完成)/异常 件数量
        OrderDashboardVO orderDashboardVO = orderFeignClient.adminDashboardOrderData();
        if (null == orderDashboardVO){
            orderDashboardVO = new OrderDashboardVO();
        }
        map.put("orderDesc", "上门取件完成：" + orderDashboardVO.getPickOrderCount() +
                "   送件上门完成：" + orderDashboardVO.getSendOrderCount() +
                "   异常订单：" + orderDashboardVO.getExceptionOrderCount());
        // 待处理反馈，日新增反馈数量
        LocalDate nowDay = LocalDate.now();
        Integer todWaitCount = userFeedbackService.getCountByStatusAndDate(FeedbackStatusEnum.WAIT, nowDay, nowDay);
        Integer allWaitCount = userFeedbackService.getCountByStatusAndDate(FeedbackStatusEnum.WAIT, null, null);
        // 日 已签到和未签到的 配送员占比饼图(暂时不写)
        map.put("feedbackDesc", "待处理反馈：" + allWaitCount + "   今日新增反馈：" + todWaitCount);
        SysUser sysUser = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysRolesLevel sysRolesLevel = sysRolesLevelService.getByUserId(sysUser.getId());
        map.put("frontName", sysUser.getUsername());
        map.put("roleName", sysRolesLevel.getRoleDesc());
        return "admin/dashboard";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/userListPage")
    public String userListPage(ModelMap map){
        SysUser id = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysUser sysUser = sysUserService.getById(id.getId());
        map.put("frontName", sysUser.getUsername());
        map.put("roleName", sysUser.getRole().getCnName());
        return "admin/user";
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

    @GetMapping("/orderListPage")
    public String orderListPage(ModelMap map){
        SysUser id = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysUser sysUser = sysUserService.getById(id.getId());
        map.put("frontName", sysUser.getUsername());
        map.put("roleName", sysUser.getRole().getCnName());
        switch (sysUser.getRole()){
            case ADMIN:
                return "admin/history";
            case COURIER:
                return "courier/history";
            default:
                return "user/history";
        }
    }

    @GetMapping("/userInfoPage")
    public String infoPage(ModelMap map){
        SysUser id = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysUser sysUser = sysUserService.getById(id.getId());
        map.put("frontName", sysUser.getUsername());
        map.put("roleName", sysUser.getRole().getCnName());

        UserInfoVo userInfo = sysUserService.getUserInfo(sysUser.getId());
        map.put("info", userInfo);
        return "common/info";

    }

    @GetMapping("/evaluatePage")
    @PreAuthorize(AuthorizeConfig.PAY_USER_AND_COURIER)
    public String evaluatePage(ModelMap map){
        SysUser id = ExpressOauth2Util.getUserJwtFromAttribute(request);
        SysUser sysUser = sysUserService.getById(id.getId());
        UserEvaluate scoreById = userEvaluateService.getScoreById(sysUser.getId());
        BigDecimal score = scoreById.getScore().divide(new BigDecimal(scoreById.getCount()), 2 , 4);
        if (scoreById.getCount() == 0){
            score = scoreById.getScore();
        }
        map.put("frontName", sysUser.getUsername());
        map.put("roleName", sysUser.getRole().getCnName());
        map.put("score", score);
        return "common/evaluate";
    }

}
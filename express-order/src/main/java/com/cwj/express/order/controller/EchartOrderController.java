package com.cwj.express.order.controller;

import com.cwj.express.api.order.EchartOrderControllerApi;
import com.cwj.express.common.enums.OrderStatusEnum;
import com.cwj.express.common.enums.OrderTypeEnum;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.order.feignclient.ucenter.UcenterFeignClient;
import com.cwj.express.order.service.OrderInfoService;
import com.cwj.express.vo.order.EchartCalendarPieItemVO;
import com.cwj.express.vo.order.OrderChartParamVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EchartOrderController extends BaseController implements EchartOrderControllerApi {

    private final UcenterFeignClient ucenterFeignClient;
    private final OrderInfoService orderInfoService;

    @Override
    @GetMapping("/orderCountEchartData")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult getOrderChartByParam(OrderChartParamVO orderChartParamVO) {
        if (!StringUtils.isEmpty(orderChartParamVO.getCourierId())){
            SysUser courier = ucenterFeignClient.getById(orderChartParamVO.getCourierId());
            if (ObjectUtils.isEmpty(courier)){
                return ResponseResult.FAIL("用户中心异常/该配送员不存在！");
            }
        }
        Map<String, EchartCalendarPieItemVO[]> mapCountByParam = orderInfoService.getMapCountByParam(orderChartParamVO);
        return ResponseResult.SUCCESS(mapCountByParam);
    }

    @Override
    @GetMapping("/getOrderCountByParam")
    public ResponseResult getOrderCountByParam(OrderChartParamVO orderChartParamVO) {
        if (!ObjectUtils.isEmpty(orderChartParamVO.getCourierId())){
            SysUser courier = ucenterFeignClient.getById(orderChartParamVO.getCourierId());
            if (ObjectUtils.isEmpty(courier)){
                return ResponseResult.FAIL("用户中心异常/该配送员不存在！");
            }
        }
        int transportCount = orderInfoService.getCountByParam(orderChartParamVO, OrderStatusEnum.COMPLETE, OrderTypeEnum.TRANSPORT);
        int pickUpCount = orderInfoService.getCountByParam(orderChartParamVO, OrderStatusEnum.COMPLETE, OrderTypeEnum.PICK_UP);
        int exceptionCount = orderInfoService.getCountByParam(orderChartParamVO, OrderStatusEnum.ERROR, null);
        Map<String, Integer> resMap = new HashMap<>();
        resMap.put("transportCount", transportCount);
        resMap.put("pickUpCount", pickUpCount);
        resMap.put("exceptionCount", exceptionCount);

        return ResponseResult.SUCCESS(resMap);
    }
}

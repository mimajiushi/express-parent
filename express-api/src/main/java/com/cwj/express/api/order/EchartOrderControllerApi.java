package com.cwj.express.api.order;

import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.vo.order.OrderChartParamVO;
import io.swagger.annotations.Api;

@Api(value="获取前端图表需要的订单信息",tags = "订单图表信息controller")
public interface EchartOrderControllerApi {

    public ResponseResult getOrderChartByParam(OrderChartParamVO orderChartParamVO);

}

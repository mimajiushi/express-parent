package com.cwj.express.api.order;

import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.vo.order.OrderChartParamVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value="获取前端图表需要的订单信息",tags = "订单图表信息controller")
public interface EchartOrderControllerApi {

    @ApiOperation("管理员获取根据查询参数获取时间内【各天】的收/取/异件数量")
    public ResponseResult getOrderChartByParam(OrderChartParamVO orderChartParamVO);

    @ApiOperation("管理员获取根据查询参数获取时间内【所有】的收/取/异件数量")
    public ResponseResult getOrderCountByParam(OrderChartParamVO orderChartParamVO);

    @ApiOperation("获取配送员排行榜（包括工资，工资计算暂时是硬编码）")
    public ResponseResult getCourierRankList(@RequestParam String startTime, @RequestParam String endTime);

}

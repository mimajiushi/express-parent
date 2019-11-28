package com.cwj.express.vo.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderChartParamVO {

    @ApiModelProperty(value = "配送员id 空则搜索全部")
    private String courierId;

    @ApiModelProperty(value = "查询开始时间", required = true)
    private String startDate;

    @ApiModelProperty(value = "查询结束时间", required = true)
    private String endDate;
}

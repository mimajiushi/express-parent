package com.cwj.express.vo.order;

import com.cwj.express.domain.order.OrderEvaluate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value="订单评价VO")
public class OrderEvaluateVO {

    @ApiModelProperty(value = "收件人")
    private Long current;

    @ApiModelProperty(value = "分页总数量")
    private Long page;

    @ApiModelProperty(value = "评价信息列表")
    private List<OrderEvaluateItemVO> record;
}

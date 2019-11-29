package com.cwj.express.vo.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: 森七柚子茶
 * @Date: 2019/11/28 18:18
 * @File : CourierRankVO.java
 * @Version 1.0
 */
@Data
public class CourierRankVO {

    @ApiModelProperty(value = "快递员")
    private String courierId;

    @ApiModelProperty(value = "快递员完成单数")
    private Integer sum;
}

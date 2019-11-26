package com.cwj.express.vo.ucenter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="仪表盘订单反馈信息对象", description="包含正在处理和未处理的反馈数量")
public class UserFeedbackVO implements Serializable {

    @ApiModelProperty(value = "正在处理的反馈数量")
    private int processCount;

    @ApiModelProperty(value = "等待处理的反馈数量")
    private int waitCount;

    @ApiModelProperty(value = "已经完成的反馈数量")
    private int finishedCount;


}
package com.cwj.express.domain.area;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 快递公司数据表
 * </p>
 *
 * @author chenwenjie
 * @since 2019-10-30
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DataCompany对象", description="快递公司数据表")
public class DataCompany implements Serializable {


    private Integer id;

    @ApiModelProperty(value = "快递公司名")
    private String name;

    @ApiModelProperty(value = "快递公司代号")
    private String code;


}

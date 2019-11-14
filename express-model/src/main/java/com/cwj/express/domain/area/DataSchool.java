package com.cwj.express.domain.area;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 高校数据表
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
@ApiModel(value="DataSchool对象", description="高校数据表")
public class DataSchool implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer id;

    @ApiModelProperty(value = "学校名称")
    private String name;

    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @ApiModelProperty(value = "学校级别")
    private String level;

    @ApiModelProperty(value = "学校网址")
    private String website;

    @ApiModelProperty(value = "学校简称")
    private String abbreviation;


}

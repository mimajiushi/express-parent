package com.cwj.express.domain.ucenter;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-12
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@ApiModel(value="UserEvaluate对象", description="用户评分表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysRolesLevel extends Model<SysRolesLevel> {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.INPUT)
    private Integer roleId;

    @ApiModelProperty(value = "角色（会员）中文描述")
    private String roleDesc;

    @ApiModelProperty(value = "角色（会员）英文名")
    private String roleName;

    @ApiModelProperty(value = "折扣")
    private BigDecimal discount;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Version
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.roleId;
    }

}

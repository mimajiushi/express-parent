package com.cwj.express.domain.order;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单评价表
 * </p>
 *
 * @author chenwenjie
 * @since 2019-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderEvaluate对象", description="订单评价表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvaluate extends Model<OrderEvaluate> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "评论是否开启（1：开启；0：关闭）")
    private Boolean hasOpen;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户评分")
    private BigDecimal userScore;

    @ApiModelProperty(value = "用户评价")
    private String userEvaluate;

    @ApiModelProperty(value = "用户评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime userDate;

    @ApiModelProperty(value = "配送员ID")
    private String courierId;

    @ApiModelProperty(value = "配送员评分")
    private BigDecimal courierScore;

    @ApiModelProperty(value = "配送员评价")
    private String courierEvaluate;

    @ApiModelProperty(value = "配送员评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime courierDate;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Version
    private LocalDateTime updateDate;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}

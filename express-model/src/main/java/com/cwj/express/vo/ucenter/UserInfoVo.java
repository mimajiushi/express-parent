package com.cwj.express.vo.ucenter;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息vo(非详细)
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfoVo {
    /**
     * 用户id
     */
    private String id;

    /**
     * 用户角色id
     */
    private Integer role;


    /**
     * 角色名
     */
    private String roleName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 正式姓名
     */
    private String realName;

    /**
     * 手机号码
     */
    private String tel;

    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 实名状态
     */
    private Integer hasReal;

    /**
     * 启用状态
     */
    private Integer hasEnable;

    /**
     * 性别
     */
    private String sex;

    /**
     * 冻结时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lockDate;

    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;



    /*
     * ==============================   请假用  ===================================
     */

    /**
     * 是否请假
     */
    private boolean leave;

    /**
     * 签到状态文字
     */
    private String signStatusStr;

    /**
     * 签到状态
     */
    private int signStatus;
}

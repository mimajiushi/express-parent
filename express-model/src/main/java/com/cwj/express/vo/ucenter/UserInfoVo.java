package com.cwj.express.vo.ucenter;


import lombok.*;
import lombok.experimental.Accessors;

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
     * 性别
     */
    private String sex;

    /**
     * 是否请假
     */
    private boolean leave;
}

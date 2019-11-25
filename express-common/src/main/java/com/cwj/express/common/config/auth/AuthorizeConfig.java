package com.cwj.express.common.config.auth;

import com.cwj.express.common.enums.SysRoleEnum;

public class AuthorizeConfig {
    /**
     * 所有付费用户角色
     */
    public static final String ALL_PAY_USER = "hasAnyRole('ROLE_USER','ROLE_VIP_USER','ROLE_SVIP_USER')";

    /**
     * 所有付费用户和配送员
     */
    public static final String PAY_USER_AND_COURIER = "hasAnyRole('ROLE_USER','ROLE_VIP_USER','ROLE_SVIP_USER','ROLE_COURIER')";

}

package com.cwj.express.api.ucenter;

import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author cwj
 */
@Api(value="获取用户信息api", description= "用户信息controller")
public interface UcenterControllerApi {

    @ApiOperation(value = "获取用户角色信息", notes = "注意！为了安全性！userId是通过解析token获取的")
    public SysRolesLevel getRoleMsgByUserId();
}

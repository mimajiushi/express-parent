package com.cwj.express.api.ucenter;

import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author cwj
 */
@Api(value="获取用户信息api", description= "用户信息controller")
public interface UcenterControllerApi {

    @ApiOperation(value = "获取用户角色信息", notes = "注意！为了安全性！userId是通过解析token获取的")
    public SysRolesLevel getRoleMsgByUserId();

    @ApiOperation(value = "获取所有配送员信息", notes = "为了开发方便，此接口暂时不需要权限验证")
    public List<SysUser> getAllCouriers();

    @ApiOperation(value = "根据用户id获取用户信息")
    public SysUser getById(@PathVariable String userId);
}

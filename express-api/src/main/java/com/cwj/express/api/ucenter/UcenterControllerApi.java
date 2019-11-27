package com.cwj.express.api.ucenter;

import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.ucenter.SysRolesLevel;
import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.vo.table.BootstrapTableVO;
import com.cwj.express.vo.ucenter.UserInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    @ApiOperation(value = "配送员请假")
    public ResponseResult courierLeave(String reason);

    @ApiOperation(value = "配送员回到岗位")
    public ResponseResult courierReWork();

    @ApiOperation(value = "配送员日常签到")
    public ResponseResult courierSignNormal();

    @ApiOperation(value = "配送员加班签到")
    public ResponseResult courierSignOT();

    @ApiOperation(value = "用户登出接口")
    public ResponseResult userLogout();

    @ApiOperation(value = "管理员条件获取用户列表")
    public BootstrapTableVO<UserInfoVo> userListByParam(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                        @RequestParam(defaultValue = "10", required = false) Integer size,
                                                        UserInfoVo userInfoVo);

    @ApiOperation(value = "管理员获取用户详情")
    public ResponseResult userInfoDetail(@PathVariable String userId, UserInfoVo userInfoVo);

    @ApiOperation(value = "获取配送员工作情况")
    public ResponseResult getCourierSignDetail(@PathVariable String courierId, String startData, String endData);
}

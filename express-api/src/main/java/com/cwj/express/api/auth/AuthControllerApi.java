package com.cwj.express.api.auth;

import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.domain.auth.request.LoginRequest;
import com.cwj.express.domain.ucenter.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.security.auth.message.AuthException;

/**
 * @author cwj
 */
@Api(value="用户认证相关api",tags = "用户认证controller")
public interface AuthControllerApi {

    @ApiOperation("用户名登录(暂时不要验证码)")
    public ResponseResult userLogin(LoginRequest loginRequest) throws AuthException;

    @ApiOperation("发送短信验证码接口-登录用")
    public ResponseResult LoginSendSms(LoginRequest loginRequest);
}

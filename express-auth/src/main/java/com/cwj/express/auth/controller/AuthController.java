package com.cwj.express.auth.controller;

import com.cwj.express.api.auth.AuthControllerApi;
import com.cwj.express.auth.config.auth.AuthConfig;
import com.cwj.express.auth.service.AuthService;
import com.cwj.express.auth.service.FaceService;
import com.cwj.express.auth.service.SmsService;
import com.cwj.express.auth.service.SysUserService;
import com.cwj.express.common.constant.LoginTypeConstant;
import com.cwj.express.common.exception.CustomException;
import com.cwj.express.common.exception.ExceptionCast;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.Response;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.utils.CookieUtil;
import com.cwj.express.common.web.BaseController;
import com.cwj.express.domain.auth.request.LoginRequest;
import com.cwj.express.domain.ucenter.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.cwj.express.utils.oldUtils.*;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController extends BaseController implements AuthControllerApi {

    private final AuthService authService;
    private final AuthConfig authConfig;
    private final SysUserService sysUserService;
    private final SmsService smsService;
    private final FaceService faceService;

    @PostMapping("/userLogin")
    @Override
    public ResponseResult userLogin(LoginRequest loginRequest) throws AuthException {

        String username = "";
        String password = "";
        String base64Prefix = "data:image/png;base64,";
        String faceDate = loginRequest.getFaceDate();

        // 后期如果代码太多就差分成各个接口，现在暂时就这么写
        switch (loginRequest.getAuthType()) {
            case LoginTypeConstant.PASSWORD:
                if(ObjectUtils.isEmpty(loginRequest) || StringUtils.isEmpty(loginRequest.getUsername())){
                    return ResponseResult.FAIL(CommonCode.AUTH_USERNAME_NONE);
                }
                if(ObjectUtils.isEmpty(loginRequest) || StringUtils.isEmpty(loginRequest.getPassword())){
                    return ResponseResult.FAIL(CommonCode.AUTH_PASSWORD_NONE);
                }
                //账号
                username = LoginTypeConstant.PASSWORD +":"+ loginRequest.getUsername();
                //密码
                password = loginRequest.getPassword();
                break;
            case LoginTypeConstant.PHONE:
                // 1.校验手机号是否存在
                boolean exist = checkPhoneNumExist(loginRequest.getPhoneNum());
                if (!exist){
                    return ResponseResult.FAIL(CommonCode.AUTH_PHONE_NOEXIST);
                }
                // 2.存在则校验验证码(存redis),验证失败则返回错误信息，成功则颁发令牌
                ResponseResult checkRes = smsService.check(loginRequest.getPhoneNum(), loginRequest.getPhoneCode());
                if (checkRes.getCode() == Response.SUCCESS_CODE){
                    username = LoginTypeConstant.PHONE + ":" + loginRequest.getPhoneNum();
                }else {
                    return checkRes;
                }
                break;
            case LoginTypeConstant.FACE:
                if (org.springframework.util.StringUtils.isEmpty(faceDate)){
                    ExceptionCast.cast(CommonCode.AUTH_FACEDATA_NONE);
                }
                if(faceDate.startsWith(base64Prefix)) {
                    faceDate = faceDate.substring(base64Prefix.length());
                }
                // username 为 id主键(为了避免过多二级索引造成空间时间浪费) ，password依然为空
                ResponseResult responseResult = faceService.faceSearchByBase64(faceDate);
                if (responseResult.getCode() != Response.SUCCESS_CODE){
                    return responseResult;
                }
                SysUser sysUser = (SysUser)responseResult.getData();
                username = LoginTypeConstant.FACE + ":" + sysUser.getId();
                break;
            default:
                break;
        }

        //申请令牌
        String authToken =  authService.login(username,password,authConfig.getClientId(),authConfig.getClientSecret());
        this.saveCookie(authToken);

        return ResponseResult.SUCCESS(authToken);
    }


    @Override
    @PostMapping("/loginSendSms")
    public ResponseResult LoginSendSms(LoginRequest loginRequest) {
        //检验手机号
        boolean exist = false;
        try {
            exist = checkPhoneNumExist(loginRequest.getPhoneNum());
        } catch (CustomException e) {
            log.error("手机验证未通过，手机号：{}，错误信息：{}", loginRequest.getPhoneNum(), e.getMessage());
            return ResponseResult.FAIL(e.getResultCode().message());
        }
        if (!exist){
            return ResponseResult.FAIL(CommonCode.AUTH_PHONE_NOEXIST);
        }
        // 发送短信
        String verifyCode = RandomUtils.number(6);
        return smsService.send(loginRequest.getPhoneNum(), verifyCode);
    }


    /**
     * 校验手机号
     * @param phoneNum 手机号
     */
    private boolean checkPhoneNumExist(String phoneNum) {
        // 校验手机号码合法
        if(!com.cwj.express.utils.oldUtils.StringUtils.isValidTel(phoneNum)){
            throw new CustomException(CommonCode.INVALID_TEL);
        }
        // 判断手机号是否被注册
        return sysUserService.phoneNumExist(phoneNum);
    }


    /**
     * 将令牌存储到cookie
     * @param token token
     */
    private void saveCookie(String token){

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response,authConfig.getCookieDomain(),"/","uid",token,authConfig.getCookieMaxAge(),false);

    }

    /**
     * 取出cookie中的身份令牌
     * @return token
     */
    private String getTokenFormCookie(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if(map!=null && map.get("uid")!=null){
            String uid = map.get("uid");
            return uid;
        }
        return null;
    }

    /**
     * 从cookie删除token
     * @param token token
     */
    private void clearCookie(String token){

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response,authConfig.getCookieDomain(),"/","uid",token,0,false);

    }
}

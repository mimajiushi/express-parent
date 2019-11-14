package com.cwj.express.auth.service.impl;


import com.cwj.express.auth.service.SysUserService;
import com.cwj.express.common.constant.LoginTypeConstant;
import com.cwj.express.domain.auth.SysUserExt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @author chenwenjie
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClientDetailsService clientDetailsService;
    private final SysUserService sysUserService;


    /**
     * 返回认证对象
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身CorsFilter份为空说明没有指定认证，走basic认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if(authentication==null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //密码
                String clientSecret = clientDetails.getClientSecret();
                return new User(username,clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        SysUserExt userext = null;
        //远程调用用户中心根据账号查询用户信息
        String[] usernameAndType = username.split(":");
        switch (usernameAndType[0]){
            case LoginTypeConstant.PASSWORD:
                userext = new SysUserExt(sysUserService.getExtByUserName(usernameAndType[1]));
                userext.setAuthType(LoginTypeConstant.PASSWORD);
                break;
            case LoginTypeConstant.PHONE:
                userext = new SysUserExt(sysUserService.getExtByTel(usernameAndType[1]));
                userext.setAuthType(LoginTypeConstant.PHONE);
                break;
            case LoginTypeConstant.FACE:
                userext = new SysUserExt(sysUserService.getById(usernameAndType[1]));
                userext.setAuthType(LoginTypeConstant.FACE);
                break;
            default:
                break;
        }

        // todo 后期如果更改数据库表还要在这里设置权限集合
        if(ObjectUtils.isEmpty(userext)){
            //返回空给spring security表示用户不存在
            return null;
        }
        return userext;
    }
}

package com.cwj.express.auth.config.auth;

import com.cwj.express.common.constant.LoginTypeConstant;
import com.cwj.express.domain.auth.SysUserExt;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;


/**
 * @author cwj
 * 小坑，用默认的DaoAuthenticationProvider会无法使用mybatis的通用枚举，后来莫名其妙好了
 * 怀疑是idea的问题
 * 重新实现的主要目的在于改写第三方登录时的密码匹配
 * */
@Slf4j
@Getter
@Setter
public class MyAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;

    /**
     * 尝试从这里入手改造第三方登录
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        SysUserExt userExt = null;
        if (userDetails instanceof SysUserExt){
            userExt = (SysUserExt)userDetails;
        }

        if (authentication.getCredentials() == null) {
            log.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();

        // 当认证方式为 httpbasic 或 password 模式时才比较密码，密码错误则抛出异常
        if ((ObjectUtils.isEmpty(userExt) || LoginTypeConstant.PASSWORD.equals(userExt.getAuthType()))
                && !passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        // 当认证方式为人脸或者短信时，记录日志
        else if (!ObjectUtils.isEmpty(userExt)
                && (LoginTypeConstant.FACE.equals(userExt.getAuthType())
                || LoginTypeConstant.PHONE.equals(userExt.getAuthType()))){
            log.debug("User logged in with non password type");
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        try {
            UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);
            if (ObjectUtils.isEmpty(loadedUser)) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        }
        catch (UsernameNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }
}

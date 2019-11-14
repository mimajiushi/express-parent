package com.cwj.express.ucenter.config.security;

import com.cwj.express.domain.ucenter.SysUser;
import com.cwj.express.utils.CookieUtil;
import com.cwj.express.utils.ExpressOauth2Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author cwj
 * JWT登录授权过滤器
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Map<String, String> uid = CookieUtil.readCookie(request, "uid");
        request.setAttribute("access_token", uid.get("uid"));
        SysUser userId = ExpressOauth2Util.getUserJwtFromAttribute(request);
        if (!StringUtils.isEmpty(userId)){
            request.setAttribute("user_id", userId.getId());
        }
        chain.doFilter(request, response);
    }
}

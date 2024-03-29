package com.cwj.express.ucenter.config.security;

import com.alibaba.fastjson.JSON;
import com.cwj.express.common.constant.URLConstant;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当未登录或者token失效访问接口时，自定义的返回结果
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json");
//        response.getWriter().println(JSON.toJSONString(ResponseResult.FAIL(CommonCode.AUTH_FALL)));
//        response.getWriter().flush();
        // 重定向到登录页
        response.sendRedirect(URLConstant.LOGIN_PAGE_URL);
    }
}

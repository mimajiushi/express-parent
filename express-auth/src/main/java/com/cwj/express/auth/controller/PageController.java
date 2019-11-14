package com.cwj.express.auth.controller;
import com.cwj.express.common.constant.SecurityConstant;
import com.cwj.express.domain.ucenter.SysUser;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ApiIgnore
@RequestMapping("/page")
public class PageController {

    /**
     * 跳转到登录页
     */
    @GetMapping("/index")
    public String showAuthenticationPage(ModelMap map) {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(ModelMap map) { return "register"; }
}
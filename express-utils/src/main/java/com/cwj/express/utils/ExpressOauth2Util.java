package com.cwj.express.utils;

import com.cwj.express.domain.ucenter.SysUser;
import lombok.Data;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by mrt on 2018/5/25.
 */
public class ExpressOauth2Util {

    public static SysUser getUserJwtFromAttribute(HttpServletRequest request){
        Map<String, String> jwtClaims = Oauth2Util.getJwtClaimsFromAttribute(request);
        if(jwtClaims == null || StringUtils.isEmpty(jwtClaims.get("id"))){
            return null;
        }
        String id = jwtClaims.get("id");
        String username = jwtClaims.get("user_name");
        //这里返回id即可
        return SysUser.builder()
                .id(id)
                .username(username).build();
    }

    public static SysUser getUserJwtFromHeader(HttpServletRequest request){
        Map<String, String> jwtClaims = Oauth2Util.getJwtClaimsFromHeader(request);
        if(jwtClaims == null || StringUtils.isEmpty(jwtClaims.get("id"))){
            return null;
        }
        String id = jwtClaims.get("id");
        String username = jwtClaims.get("user_name");
        //这里返回id即可
        return SysUser.builder()
                .id(id)
                .username(username).build();
    }

}

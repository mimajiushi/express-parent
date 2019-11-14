package com.cwj.express.utils;


import com.alibaba.fastjson.JSON;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by mrt on 2018/5/25.
 */
public class Oauth2Util {

    /**
     * todo 后期如果整合网关， 就在网关那一层改成请求头传递
     * @param request
     * @return
     */
    public static Map<String,String> getJwtClaimsFromAttribute(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
//        //取出头信息
//        String authorization = request.getHeader("Authorization");
//        if (StringUtils.isEmpty(authorization) || authorization.indexOf("Bearer") < 0) {
//            return null;
//        }
//        //从Bearer 后边开始取出token
//        String token = authorization.substring(7);

        // 因为自定过滤器把token存在 Attribute 了，所以这里从 Attribute 取即可
        String access_token = (String) request.getAttribute("access_token");
        if (StringUtils.isEmpty(access_token)){
            return null;
        }

        Map<String,String> map = null;
        try {
            //解析jwt
            Jwt decode = JwtHelper.decode(access_token);
            //得到 jwt中的用户信息
            String claims = decode.getClaims();
            //将jwt转为Map
            map = JSON.parseObject(claims, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String,String> getJwtClaimsFromHeader(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        //取出头信息
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization) || authorization.indexOf("Bearer") < 0) {
            return null;
        }
        //从Bearer 后边开始取出token
        String access_token = authorization.substring(7);

        Map<String,String> map = null;
        try {
            //解析jwt
            Jwt decode = JwtHelper.decode(access_token);
            //得到 jwt中的用户信息
            String claims = decode.getClaims();
            //将jwt转为Map
            map = JSON.parseObject(claims, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) {
        Jwt decode = JwtHelper.decode("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE1NzE3MTAxNjUsImF1dGhvcml0aWVzIjpbInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfZGVsIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9saXN0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9wbGFuIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZSIsImNvdXJzZV9maW5kX2xpc3QiLCJ4Y190ZWFjaG1hbmFnZXIiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX21hcmtldCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcHVibGlzaCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIl0sImp0aSI6IjZmZDgzOWVkLTI3MzUtNGVjNC1hNjZkLTU1MzkxNGU0YmE1ZCIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.PNDQ_NJFS4gzQbS3lI0cRuOqmQ1wKhQvMec-yHSfPhSAqcVzQ5EvJX2D0I1UI7uh5GNybwu-JO0AR0uZ_vOnS1BR6n1rb0mwZR7v8aZa2fZGJ8nQ4IH8f0c0wnoOOpPCfmBOplZ-x-oD25C_kbvzyCIgxH5BxzJ0Q09MW5vfZfdikSa-x7Hg-t2gHrYAFgvHq_mGRbihQluUCw4zd_Td2dfxjcQfO90QimF3rKd8VtI9p9eiQ-Vpf1SmybCqrdwtPVV7YzJklzVb2EDMJcJS79v7air_fJ2u4_bNqfDwIJdh_2MWISKYkZI__aJq_7M0j1HQHGgm9C24RwqKluMyBA");
        String claims = decode.getClaims();
        Map map = JSON.parseObject(claims, Map.class);
        map.forEach((k,y)->{
            System.out.println(k + ": " + y);
        });

    }
}

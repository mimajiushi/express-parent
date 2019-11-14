package com.cwj.express.common.Interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * @author cwj
 * @deprecated 因为用feign替代，这部分就不使用了,但代码还是留着
 */

public class RestTempleInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();
        String token = (String) request.getAttribute("access_token");
        HttpHeaders headers = httpRequest.getHeaders();
        if (!StringUtils.isEmpty(token)){
            headers.add("Authorization", "Bearer " + token);
        }

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}

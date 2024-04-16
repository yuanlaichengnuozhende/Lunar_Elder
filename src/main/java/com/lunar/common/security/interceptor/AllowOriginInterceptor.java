package com.lunar.common.security.interceptor;

import com.lunar.common.security.resolver.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 若使用nginx配置，则不需要
 */
@Component
public class AllowOriginInterceptor implements HandlerInterceptor {

    @Value("${http.cors.domain:*}")
    private String allowValue;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String origin = request.getHeader("origin");
        String headers = request.getHeader("Access-Control-Request-Headers");
        if (StringUtils.isNotEmpty(headers)) {
            headers = ", " + headers;
        } else {
            headers = "";
        }

        if (StringUtils.isNotEmpty(origin)) {
            if (originIsAllow(origin, allowValue)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Headers",
                                   "Origin, X-Requested-With, Content-Type, Accept" + headers);
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                response.setHeader("Access-Control-Allow-Credentials", "true");
            }
        }

        //OPTIONS值需要返回跨域头，不需要查数据
        if ("OPTIONS".equals(request.getMethod())) {
            // ApiResult.ok() 如果使用JsonWriter，需要处理doWrite方法
//            JsonWriter.write(response, ApiResult.ok());
            JsonWriter.write(response, "");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {

    }

    private boolean originIsAllow(String origin, String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        if (("*").equals(value)) {
            return true;
        }
        String[] values = value.split(",");
        for (String s : values) {
            if (origin.indexOf(s) > -1) {
                return true;
            }
        }
        return false;
    }
}

package com.lunar.common.security.interceptor;

import cn.hutool.core.convert.Convert;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.context.SecurityContextHolder;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.security.annotation.AllowNoLogin;
import com.lunar.common.security.auth.AuthUtil;
import com.lunar.common.security.utils.SecurityUtils;
import com.google.common.collect.Lists;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.context.SecurityContextHolder;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 */
@Slf4j
public class HeaderInterceptor implements AsyncHandlerInterceptor {

    /** 以下方法名不校验是否为默认密码 */
    public static final List<String> EXCLUDE_METHOD = Lists.newArrayList("modifyPassword", "modifyDefaultPassword");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Class clazz = handlerMethod.getBeanType();
        AllowNoLogin lv = AnnotationUtils.findAnnotation(clazz, AllowNoLogin.class);
        // 加了AllowNoLogin注解，通过
        if (lv != null) {
            return true;
        }

        Method method = handlerMethod.getMethod();
        lv = AnnotationUtils.findAnnotation(method, AllowNoLogin.class);
        if (lv != null) {
            return true;
        }

        String token = SecurityUtils.getToken();
        if (StringUtils.isBlank(token)) {
            throw new ServiceException(ApiCode.TOKEN_ERROR);
        }

        LoginUser loginUser = AuthUtil.getLoginUser(token);
        if (loginUser == null) {
            throw new ServiceException(ApiCode.TOKEN_ERROR);
        }

        // 默认密码，强制需要修改
        if (!StringUtils.matches(method.getName(), EXCLUDE_METHOD)
            && Convert.toBool(loginUser.getDefaultPassword(), true)) {
            throw new ServiceException(SystemCode.DEFAULT_PASSWORD);
        }

        // 验证token并刷新有效期
        AuthUtil.refreshTokenIfExpireShort(loginUser);
        SecurityContextHolder.setUserId(Convert.toStr(loginUser.getUserId()));
        SecurityContextHolder.setUsername(Convert.toStr(loginUser.getUsername()));
        SecurityContextHolder.setCompanyId(Convert.toStr(loginUser.getCompanyId()));
        SecurityContextHolder.setOrgId(Convert.toStr(loginUser.getOrgId()));
        SecurityContextHolder.set(SecurityConsts.LOGIN_USER, loginUser);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        SecurityContextHolder.remove();
    }
}

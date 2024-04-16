package com.lunar.common.security.aspect;

import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.exception.InnerAuthException;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.security.annotation.InnerAuth;
import com.lunar.common.core.consts.SecurityConsts;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.security.annotation.InnerAuth;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.core.Ordered;

/**
 * 内部服务调用验证处理
 */
//@Aspect
//@Component
public class InnerAuthAspect implements Ordered {

    @Around("@annotation(innerAuth)")
    public Object innerAround(ProceedingJoinPoint point, InnerAuth innerAuth) throws Throwable {
        // 只允许内部调用
        String source = ServletUtils.getRequest().getHeader(SecurityConsts.FROM_SOURCE);
        // 内部请求验证
        if (!StringUtils.equals(SecurityConsts.INNER, source)) {
            throw new InnerAuthException("没有内部访问权限，不允许访问");
        }

        String userid = ServletUtils.getRequest().getHeader(SecurityConsts.USER_ID);
        String username = ServletUtils.getRequest().getHeader(SecurityConsts.USERNAME);
        // 用户信息验证
        if (innerAuth.isUser() && (StringUtils.isAnyBlank(userid, username))) {
            throw new InnerAuthException("没有设置用户信息，不允许访问 ");
        }
        return point.proceed();
    }

    /**
     * 确保在权限认证aop执行前执行
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}

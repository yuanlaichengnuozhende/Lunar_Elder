package com.lunar.common.security.aspect;

import com.lunar.common.security.annotation.RequiresLogin;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.annotation.RequiresRoles;
import com.lunar.common.security.auth.AuthUtil;
import com.lunar.common.security.annotation.RequiresLogin;
import com.lunar.common.security.annotation.RequiresPermissions;
import com.lunar.common.security.annotation.RequiresRoles;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限aspect
 */
@Aspect
@Component
public class PreAuthorizeAspect {

    /**
     * 构建
     */
    public PreAuthorizeAspect() {
    }

    /**
     * 定义AOP签名 (切入所有使用鉴权注解的方法)
     */
    public static final String POINTCUT_SIGN =
        "@annotation(com.carbonstop.common.security.annotation.RequiresLogin) "
            + "|| @annotation(com.carbonstop.common.security.annotation.RequiresPermissions) "
            + "|| @annotation(com.carbonstop.common.security.annotation.RequiresRoles)";

    /**
     * 声明AOP签名
     */
    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 注解鉴权
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        checkMethodAnnotation(signature.getMethod());

        try {
            // 执行原有逻辑
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * 对一个Method对象进行注解检查
     */
    public void checkMethodAnnotation(Method method) {
        // 校验 @RequiresLogin 注解
        RequiresLogin requiresLogin = method.getAnnotation(RequiresLogin.class);
        if (requiresLogin != null) {
            AuthUtil.checkLogin();
        }

        // 校验 @RequiresPermissions 注解
        RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
        if (requiresPermissions != null) {
            AuthUtil.checkPerms(requiresPermissions);
        }

        // 校验 @RequiresRoles 注解
        RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
        if (requiresRoles != null) {
            AuthUtil.checkRoles(requiresRoles);
        }
    }

}

package com.lunar.common.log.aspect;

import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.log.annotation.Log;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.OperLog;
import com.lunar.system.service.OperLogService;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.model.LoginUser;
import com.lunar.common.core.utils.IpUtils;
import com.lunar.common.core.utils.ServletUtils;
import com.lunar.common.core.utils.StringUtils;
import com.lunar.common.log.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 操作日志记录处理
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class LogAspect {

    @Autowired
    private OperLogService operLogService;

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, jsonResult);
    }

    protected void handleLog(final JoinPoint joinPoint, Log logAnno, Object jsonResult) {
        try {
            if (!logAnno.needContent()) {
                log.info("不需要记录日志");
                return;
            }

            String logContent = null;
            if (jsonResult instanceof ApiResult) {
                ApiResult apiResult = (ApiResult) jsonResult;
                logContent = apiResult.getLog();
            }

            // 操作明细但明细为空时，不记录日志
            if (StringUtils.isBlank(logContent)) {
//                log.info("无操作明细，不记录操作日志");
                return;
            }

            LoginUser loginUser = SecurityUtils.getLoginUser();

            String ipAddr = IpUtils.getRemoteIpAddr(ServletUtils.getRequest());

            OperLog operLog = OperLog.builder()
                .operType(logAnno.operType())
                .moduleType(logAnno.moduleType())
                .content(logContent)
                .username(loginUser.getUsername())
                .realName(loginUser.getRealName())
                .ipAddr(ipAddr)
                .createBy(loginUser.getUserId())
                .createTime(new Date())
                .build();

            operLogService.insertSelective(operLog);
        } catch (Exception e) {
            log.error("记录操作日志异常", e);
        }
    }

}

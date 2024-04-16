package com.lunar.common.security.handler;

import cn.hutool.http.HttpStatus;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import com.lunar.common.core.exception.InnerAuthException;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.exception.auth.NotPermissionException;
import com.lunar.common.core.exception.auth.NotRoleException;
import com.lunar.common.redis.utils.I18nUtil;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.domain.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

/**
 * 全局异常处理器
 */
@SuppressWarnings("ALL")
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public ApiResult handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        // 缺失的权限
        String missPerm = e.getMissPerm();
        log.error("请求地址[{}],缺失权限[{}],权限码校验失败[{}]", requestUri, missPerm, e.getMessage());
        return ApiResult.error(HttpStatus.HTTP_FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ApiResult handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        // 缺失的权限
        String missRole = e.getMissRole();
        log.error("请求地址[{}],缺失角色[{}],角色校验失败[{}]", requestUri, missRole, e.getMessage());
        return ApiResult.error(HttpStatus.HTTP_FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                         HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址[{}],不支持[{}]请求", requestUri, e.getMethod());
        return ApiResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public ApiResult handleServiceException(ServiceException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址[{}],业务异常[{}]", requestUri, e.getMessage());

        Integer code = e.getCode();
        String msg = e.getMessage();
        if (code == null) {
            return ApiResult.error(msg);
        }

        // 查询用户当前语言
        String lang = I18nUtil.getUserLang(SecurityUtils.getUserId());

        // 查询错误码翻译
        msg = I18nUtil.getMsgDefaultIfBlank(lang, String.valueOf(code), msg);

        if (Objects.nonNull(e.getData())) {
            return ApiResult.error(code, msg, e.getData());
        } else {
            return ApiResult.error(code, msg);
        }
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public ApiResult handleBindException(BindException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址[{}],自定义验证异常[{}]", requestUri, e.getMessage());

        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ApiResult.error(message);
    }

    /**
     * 参数校验
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址[{}],参数校验异常[{}]", requestUri, e.getMessage());

//        StringBuilder msg = new StringBuilder();
//        Iterator<ConstraintViolation<?>> it = e.getConstraintViolations().iterator();
//        while (it.hasNext()) {
//            msg.append(it.next().getMessage());
//            if (it.hasNext()) {
//                msg.append(", ");
//            }
//        }
//
//        return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR, msg.toString());

        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().iterator().next();
        return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR, constraintViolation.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址[{}],自定义验证异常[{}]", requestUri, e.getMessage());

//        StringBuilder msg = new StringBuilder();
//        Iterator<ObjectError> it = bindingResult.getAllErrors().iterator();
//        while (it.hasNext()) {
//            msg.append(it.next().getDefaultMessage());
//            if (it.hasNext()) {
//                msg.append(", ");
//            }
//        }

        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String defaultMessage = fieldError.getDefaultMessage();

        return ApiResult.error(ApiCode.REQUEST_PARAM_ERROR, defaultMessage);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResult handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址[{}],发生未知异常.", requestUri, e);
        return ApiResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址[{}],发生系统异常.", requestUri, e);

//        Map<String, Object> exceptionInfo = getExceptionInfo(e);
        return ApiResult.error(ApiCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 内部认证异常
     */
    @ExceptionHandler(InnerAuthException.class)
    public ApiResult handleInnerAuthException(InnerAuthException e) {
        log.error("内部认证异常");
        return ApiResult.error(e.getMessage());
    }

}

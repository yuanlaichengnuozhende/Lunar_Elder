package com.lunar.common.core.code;

import java.io.Serializable;

/**
 * 通用返回码
 *
 * @author szx
 */
public enum ApiCode implements BaseCode, Serializable {

    SUCCESS(200, "success"),
    FAIL(500, "fail"),

    TOKEN_ERROR(600, "token校验失败"),
    REQUEST_PARAM_ERROR(601, "传入参数错误"),
    REQUEST_PARAM_MISSING(602, "传入参数缺失"),
    DATA_ERROR(610, "数据错误"),
    DATA_MISS(611, "数据不存在"),

    INTERNAL_SERVER_ERROR(1001, "服务器错误"),
    SERVICE_UNAVAILABLE(1002, "操作失败，请重试"),
    THIRD_SERVER_ERROR(1003, "第三方服务器错误"),
    INVOKE_SERVICE_FAIL(1004, "调用服务失败"),
    BIZ_EXCEPTION(1005, "业务异常"),
    TOO_MANY_REQUESTS(1006, "请求次数超过限制"),
    COOKIE_ERROR(1007, "cookie错误"),
    UNAUTHORIZED(1008, "未认证"),
    FORBIDDEN(1009, "没有权限访问"),
    NOT_FOUND(1010, "请求资源不存在"),
    BAD_REQUEST(1011, "请求错误"),

    SIGN_ERROR(2001, "签名校验失败"),
    LOGIN_FAIL(2002, "登录失败"),
    USER_ERROR(2003, "用户校验失败"),
    GET_ENUM_ERROR(2006, "获取枚举类失败"),
    DATA_STATUS_ERROR(2007, "数据状态错误"),
    DATA_PERMISSION_ERROR(2008, "数据权限错误"),
    COMPANY_PERMS_ERROR(2009, "公司权限错误"),
    ORG_PERMS_ERROR(2010, "组织权限错误"),
    DECRYPT_ERROR(2011, "解密数据失败"),
    AUDIT_FORBIDDEN(2022, "您没有审批权限"),
    AUDIT_DATA_ERROR(2023, "审批数据错误"),
    MAIL_ERROR(2030, "邮箱格式错误"),
    SSO_LOGIN_FAILED(2031, "SSO登录失败"),

    FILE_UPLOAD_FAIL(3001, "文件上传失败"),
    FILE_GENERATE_FAILED(3002, "文件生成失败"),
    FILE_CHECK_FAILED(3003, "文件校验失败"),

    CAPTCHA_MISSING(2050, "请输入验证码"),
    GENERATE_CAPTCHA_FAILED(2051, "生成验证码失败"),
    CAPTCHA_ERROR(2052, "验证码错误"),

    AUDIT_COMMENT_MISS(3001, "请填写审核备注"),

    ;

    private Integer code;
    private String message;

    ApiCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

}
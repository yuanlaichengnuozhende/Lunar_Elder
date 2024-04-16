package com.lunar.common.core.code;

import java.io.Serializable;

/**
 * 第三方业务返回码
 *
 * @author szx
 */
public enum ThirdCode implements BaseCode, Serializable {

    INVALID_MOBILE(10001, "手机号错误"),
    VERIFY_CODE_EXIST(10002, "60秒内只能生成一次验证码"),
    INVALID_VERIFY_CODE(10003, "验证码无效"),
    VERIFY_CODE_FAILED_TOO_MANY(10004, "验证码错误次数过多"),
    VERIFY_CODE_ERROR(10005, "验证码错误"),
    SMS_SEND_ERROR(10006, "短信发送失败"),
    VERIFY_CODE_NOT_FOUND(10007, "缺少验证码"),

    WX_GET_USERINFO_BY_CODE(10100, "网页授权获取用户信息异常"),
    WX_DECRYPT_DATA_FAILED(10101, "解密微信数据失败"),
    WX_AUTH_FAILED(10102, "微信凭证校验失败"),
    WX_LOGIN_FAILED(10103, "微信登录失败"),
    WX_SUB_ACODE_FAILED(10104, "获取微信二维码失败"),
    WX_STEP_FAILED(10105, "获取微信步数失败"),
    WX_ACCESS_TOKEN_FAILED(10106, "获取微信accessToken失败"),
    WX_USER_PHONENUMBER_FAILED(10107, "获取微信用户手机号失败"),

    BAIDU_ACCESS_TOKEN_FAILED(10200, "获取百度accessToken失败"),
    BAIDU_IMAGE_CLASSIFY_FAILED(10201, "图像识别失败"),
    BAIDU_IMAGE_CLASSIFY_EMPTY(10202, "未识别到图像"),

    DING_LOGIN_FAILED(10300, "钉钉登录失败"),
    DING_ACCESS_TOKEN_FAILED(10301, "获取钉钉accessToken失败"),
    DING_USER_INFO_FAILED(10302, "获取钉钉用户信息失败"),
    DING_USER_DETAIL_FAILED(10303, "获取钉钉用户详情失败"),
    DING_STEP_FAILED(10304, "获取钉钉用户步数失败"),
    DING_CALENDAR_FAILED(10305, "获取钉钉日程失败"),
    DING_MEETING_FAILED(10306, "获取企业视频会议明细列表失败"),
    DING_MEETING_DETAIL_FAILED(10307, "获取视频会议详情失败"),
    DING_AUTH_CODE_FAILED(10308, "登录第三方网站回调失败"),
    DING_GET_USER_BY_TOKEN_FAILED(10309, "根据第三方回调token获取用户信息失败"),
    DING_GET_USERID_FAILED(10310, "根据unionid获取用户userid失败"),
    DING_SEND_MSG_ACTION_CARD_FAILED(10311, "钉钉发送卡片消息工作通知失败"),
    DING_SEND_RESULT_FAILED(10312, "获取工作通知消息的发送结果失败"),

    ;

    private Integer code;
    private String message;

    ThirdCode(Integer code, String message) {
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
package com.lunar.common.core.domain;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.BaseCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 响应信息主体
 *
 * @author szx
 */
@Data
@ApiModel("通用接口返回对象")
public class ApiResult<T> {

    /**
     * 成功
     */
    public static final int SUCCESS = ApiCode.SUCCESS.getCode();
    public static final String SUCCESS_MSG = ApiCode.SUCCESS.getMessage();

    /**
     * 失败
     */
    public static final int FAIL = ApiCode.FAIL.getCode();
    public static final String FAIL_MSG = ApiCode.FAIL.getMessage();

    /**
     * 状态: 200,500
     */
    @ApiModelProperty(required = true, notes = "结果码", example = "200")
    private int code;

    /**
     * 返回信息
     */
    @ApiModelProperty(required = true, notes = "返回信息", example = "success")
    private String msg;

    /**
     * 返回数据
     */
    @ApiModelProperty(required = true, notes = "返回数据")
    private T data;

    /**
     * 操作日志
     */
    @JsonIgnore
    private String log;

    public ApiResult() {
    }

    public ApiResult(int code, String msg, T data, String log) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.log = log;
    }

    public static <T> ApiResult<T> ok() {
        return new ApiResult<>(SUCCESS, SUCCESS_MSG, null, null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(SUCCESS, SUCCESS_MSG, data, null);
    }

    public static <T> ApiResult<T> ok(T data, String msg) {
        return new ApiResult<>(SUCCESS, msg, data, null);
    }

    public static <T> ApiResult<T> error() {
        return new ApiResult<>(FAIL, FAIL_MSG, null, null);
    }

    public static <T> ApiResult<T> error(String msg) {
        return new ApiResult<>(FAIL, msg, null, null);
    }

    public static <T> ApiResult<T> error(T data) {
        return new ApiResult<>(FAIL, FAIL_MSG, data, null);
    }

    public static <T> ApiResult<T> error(int code, String msg) {
        return new ApiResult<>(code, msg, null, null);
    }

    public static <T> ApiResult<T> error(int code, String msg, T data) {
        return new ApiResult<>(code, msg, data, null);
    }

    public static <T> ApiResult<T> error(BaseCode baseCode) {
        return new ApiResult<>(baseCode.getCode(), baseCode.getMessage(), null, null);
    }

    public static <T> ApiResult<T> error(BaseCode baseCode, T data) {
        return new ApiResult<>(baseCode.getCode(), baseCode.getMessage(), data, null);
    }

    public static <T> ApiResult<T> log(String log) {
        return new ApiResult<>(SUCCESS, SUCCESS_MSG, null, log);
    }

    public static <T> ApiResult<T> log(T data, String log) {
        return new ApiResult<>(SUCCESS, SUCCESS_MSG, data, log);
    }

    public static <T> ApiResult<T> log(T data, String msg, String log) {
        return new ApiResult<>(SUCCESS, msg, data, log);
    }

    @JsonIgnore
    public T getSuccessData() {
        if (code != SUCCESS) {
            return null;
        }
        return data;
    }

    @JsonIgnore
    public boolean isOk() {
        return code == SUCCESS;
    }

    @JsonIgnore
    public boolean isError() {
        return !isOk();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return isError() || data == null;
    }

}

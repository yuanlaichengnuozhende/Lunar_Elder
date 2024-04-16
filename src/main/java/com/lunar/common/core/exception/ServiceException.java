package com.lunar.common.core.exception;

import com.lunar.common.core.code.BaseCode;
import com.lunar.common.core.code.BaseCode;
import lombok.Getter;

/**
 * @author szx
 */
@Getter
public class ServiceException extends BaseException {

    private Integer code;

    private Object data;

    public ServiceException() {

    }

    public ServiceException(String message) {
        super(message);
        this.code = 500;
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(Integer code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public ServiceException(BaseCode code) {
        super(code.getMessage());
        this.code = code.getCode();
    }

    public ServiceException(BaseCode code, String message) {
        super(message);
        this.code = code.getCode();
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public ServiceException(Throwable cause) {
        super(cause);
        this.code = 500;
    }

}

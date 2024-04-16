package com.lunar.common.core.exception.auth;

import lombok.Getter;

/**
 * 未能通过的权限认证异常
 */
@Getter
public class NotPermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 缺失的权限，只取第一个
     */
    private String missPerm;

    public NotPermissionException() {
    }

    public NotPermissionException(String missPerm) {
        this.missPerm = missPerm;
    }

//    public NotPermissionException(String[] permissions) {
//        super(StringUtils.join(permissions, ","));
//    }

}

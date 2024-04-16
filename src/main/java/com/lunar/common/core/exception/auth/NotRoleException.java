package com.lunar.common.core.exception.auth;

import lombok.Getter;

/**
 * 未能通过的角色认证异常
 */
@Getter
public class NotRoleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 缺失的角色，只取第一个
     */
    private String missRole;

    public NotRoleException() {
    }

    public NotRoleException(String missRole) {
        this.missRole = missRole;
    }

    //    public NotRoleException(String[] roles) {
//        super(StringUtils.join(roles, ","));
//    }

}

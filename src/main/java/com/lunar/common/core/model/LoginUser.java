package com.lunar.common.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * userId
     */
    private Long userId;

    /**
     * 用户名（工号）
     */
    private String username;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间 ms
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipAddr;

    /**
     * 权限perms列表
     */
    private Set<String> permissions;

    /**
     * 角色id列表
     */
    private Set<String> roles;

    /**
     * token
     */
    private String token;

    /**
     * 是否默认密码
     */
    private Boolean defaultPassword;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录代码
     */
    private String companyCode;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 用户当前组织id
     */
    private Long orgId;

    /**
     * 用户当前组织名称
     */
    private String orgName;

    /**
     * 根组织org
     */
    private OrgPojo rootOrg;

    /**
     * 用户组织列表（当前组织及下级组织）
     */
    private List<OrgPojo> orgList;

}

package com.lunar.system.query;

import com.lunar.common.mybatis.query.Query;
import com.lunar.system.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQuery implements Query {

    private Long id;
    private Long companyId;
    private String username;
    private String realName;
    private String mobile;
    private String email;
    private String password;
    private Date lastLoginTime;
    private UserStatus userStatus;
    private Boolean defaultPassword;
    private Boolean adminFlag;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private String likeUsername;
    private String likeRealName;
    private String likeMobile;
    private String likeEmail;
    private String likeUserInfo;

    private Long neId;

    private Collection<Long> idList;
    private Collection<Long> orgIdList;

    private Long orgId;
    private Long roleId;

}
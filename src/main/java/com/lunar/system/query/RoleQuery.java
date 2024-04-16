package com.lunar.system.query;

import com.lunar.common.mybatis.query.Query;
import com.lunar.system.enums.RoleType;
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
public class RoleQuery implements Query {

    private Long id;
    private Long companyId;
    private String roleName;
    private String roleInfo;
    private RoleType roleType;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Collection<Long> idList;

    private String likeRoleName;

}
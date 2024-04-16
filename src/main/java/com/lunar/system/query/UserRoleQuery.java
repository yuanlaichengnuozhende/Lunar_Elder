package com.lunar.system.query;

import com.lunar.common.mybatis.query.Query;
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
public class UserRoleQuery implements Query {

    private Long id;
    private Long userId;
    private Long roleId;
    private Long createBy;
    private Date createTime;

    private Long neId;

    private Collection<Long> idList;
    private Collection<Long> userIdList;
    private Collection<Long> roleIdList;

}
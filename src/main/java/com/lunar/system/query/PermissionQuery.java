package com.lunar.system.query;

import com.lunar.common.mybatis.query.Query;
import com.lunar.system.enums.MenuType;
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
public class PermissionQuery implements Query {

    private Long id;
    private String permissionName;
    private Long pid;
    private String path;
    private MenuType menuType;
    private String perms;
    private Integer orderNum;
    private String icon;
    private String remark;
    private Boolean deleted;
    private Long createBy;
    private Long updateBy;
    private Date createTime;
    private Date updateTime;

    private Collection<Long> idList;

}
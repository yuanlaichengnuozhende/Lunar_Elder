package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.RolePermission;

public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * @param roleId
     * @return
     */
    int deleteByRoleId(Long roleId);

}
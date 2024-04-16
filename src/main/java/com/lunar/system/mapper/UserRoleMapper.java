package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * @param userId
     * @return
     */
    int deleteByUserId(Long userId);

    /**
     * @param roleId
     * @return
     */
    int deleteByRoleId(Long roleId);

}
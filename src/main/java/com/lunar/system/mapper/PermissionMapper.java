package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.Permission;
import com.lunar.system.response.PermissionResp;
import com.lunar.system.response.PermissionResp;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 用户所有权限=左侧权限树
     *
     * @param userId
     * @return
     */
    List<PermissionResp> findUserPermList(Long userId);

    /**
     * 删除
     *
     * @param pid
     * @return
     */
    int deleteByPid(Long pid);

}
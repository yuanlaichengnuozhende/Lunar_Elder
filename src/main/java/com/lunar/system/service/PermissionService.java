package com.lunar.system.service;

import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.Permission;
import com.lunar.system.response.PermissionResp;
import com.lunar.system.response.PermissionResp;

import java.util.List;

public interface PermissionService extends BaseService<Permission> {

    /**
     * 缓存权限点名称
     * <p>
     * key: path
     * <p>
     * value: permission_name
     */
    void reCache();

    /**
     * 用户所有权限=左侧权限树
     *
     * @param userId
     * @return
     */
    List<PermissionResp> findUserPermList(Long userId);

    /**
     * 删除pid权限
     *
     * @param pid
     * @return
     */
    int deleteByPid(Long pid);

    /**
     * @param permission
     */
    void delete(Permission permission);
}

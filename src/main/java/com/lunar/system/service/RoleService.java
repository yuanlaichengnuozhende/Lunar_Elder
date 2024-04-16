package com.lunar.system.service;

import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.Role;
import com.lunar.system.request.RoleReq;

public interface RoleService extends BaseService<Role> {

    /**
     * 新增角色
     *
     * @param req
     * @return
     */
    Role add(RoleReq req);

    /**
     * 编辑角色
     *
     * @param req
     * @return
     */
    Role edit(RoleReq req);

    /**
     * 删除角色
     *
     * @param role
     */
    void delete(Role role);

}

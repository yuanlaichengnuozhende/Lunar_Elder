package com.lunar.system.service.impl;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Role;
import com.lunar.system.entity.RolePermission;
import com.lunar.system.enums.RoleType;
import com.lunar.system.mapper.RoleMapper;
import com.lunar.system.mapper.RolePermissionMapper;
import com.lunar.system.mapper.UserRoleMapper;
import com.lunar.system.query.RoleQuery;
import com.lunar.system.query.UserRoleQuery;
import com.lunar.system.request.RoleReq;
import com.lunar.system.service.RoleService;
import com.google.common.collect.Lists;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.enums.OptionType;
import com.lunar.system.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role>
    implements RoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role add(RoleReq req) {
        log.info("新增角色. req={}", req);

        // 校验name
        int count = this.count(RoleQuery.builder()
                                   .companyId(SecurityUtils.getCompanyId())
                                   .roleName(req.getRoleName())
                                   .deleted(false)
                                   .build());
        if (count > 0) {
            throw new ServiceException(SystemCode.ROLE_NAME_EXIST);
        }

        Long userId = SecurityUtils.getUserId();

        Role role = Role.builder()
            .companyId(SecurityUtils.getCompanyId())
            .roleName(req.getRoleName())
            .roleInfo(req.getRoleInfo())
            .roleType(RoleType.CUSTOM)
            .deleted(false)
            .createBy(userId)
            .updateBy(userId)
            .build();
        this.insertSelective(role);

        // 创建关系
        buildRel(req.getAllCheckedList(), req.getHalfCheckedList(), role.getId(), userId);

        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role edit(RoleReq req) {
        log.info("编辑角色. req={}", req);

        Role role = this.selectOne(
            RoleQuery.builder().id(req.getId()).companyId(SecurityUtils.getCompanyId()).deleted(false).build());
        if (role == null) {
            throw new ServiceException(ApiCode.DATA_ERROR);
        }

        // 预置角色不允许编辑
        if (role.getRoleType() == RoleType.PRESET) {
            throw new ServiceException(SystemCode.PRESET_ROLE_CANNOT_EDIT);
        }

        // 角色名校验
        if (!StringUtils.equals(role.getRoleName(), req.getRoleName())) {
            int count = this.count(RoleQuery.builder()
                                       .companyId(SecurityUtils.getCompanyId())
                                       .roleName(req.getRoleName())
                                       .deleted(false)
                                       .build());
            if (count > 0) {
                throw new ServiceException(SystemCode.ROLE_NAME_EXIST);
            }
        }

        Long userId = SecurityUtils.getUserId();
        Long roleId = role.getId();

        role.setRoleName(req.getRoleName());
        role.setRoleInfo(req.getRoleInfo());
        role.setUpdateBy(userId);
        this.updateSelective(role);

        // 删除角色权限
        rolePermissionMapper.deleteByRoleId(roleId);

        // 创建新关系
        buildRel(req.getAllCheckedList(), req.getHalfCheckedList(), roleId, userId);

        log.info("编辑角色完成");

        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Role role) {
        log.info("删除角色. role={}", role);

        // 角色下有用户
        int count = userRoleMapper.count(UserRoleQuery.builder().roleId(role.getId()).build());
        if (count > 0) {
            throw new ServiceException(SystemCode.ROLE_BIND_USER);
        }

        // 删除权限关系
        rolePermissionMapper.deleteByRoleId(role.getId());

        // 删除用户关系
        userRoleMapper.deleteByRoleId(role.getId());

        role.setDeleted(true);
        role.setUpdateBy(SecurityUtils.getUserId());
        this.updateSelective(role);

//        // 禁用没有角色的用户
//        userService.disableUserNonRole();

        log.info("删除角色成功");
    }

    /**
     * 创建关系
     *
     * @param allCheckedList
     * @param halfCheckedList
     */
    private void buildRel(List<Long> allCheckedList, List<Long> halfCheckedList, Long roleId, Long createBy) {
        List<RolePermission> entityList = Lists.newArrayList();
        Date now = new Date();
        if (CollectionUtils.isNotEmpty(allCheckedList)) {
            allCheckedList.forEach(x -> {
                RolePermission entity = RolePermission.builder()
                    .roleId(roleId)
                    .permissionId(x)
                    .optionType(OptionType.ALL)
                    .createBy(createBy)
                    .createTime(now)
                    .build();
                entityList.add(entity);
            });
        }
        if (CollectionUtils.isNotEmpty(halfCheckedList)) {
            halfCheckedList.forEach(x -> {
                RolePermission entity = RolePermission.builder()
                    .roleId(roleId)
                    .permissionId(x)
                    .optionType(OptionType.HALF)
                    .createBy(createBy)
                    .createTime(now)
                    .build();
                entityList.add(entity);
            });
        }

        if (CollectionUtils.isNotEmpty(entityList)) {
            rolePermissionMapper.batchInsert(entityList);
        }
    }

}
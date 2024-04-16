package com.lunar.system.service.impl;

import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.redis.utils.RedisUtil;
import com.lunar.system.entity.Permission;
import com.lunar.system.mapper.PermissionMapper;
import com.lunar.system.response.PermissionResp;
import com.lunar.system.service.PermissionService;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.system.response.PermissionResp;
import com.lunar.system.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionServiceImpl extends BaseServiceImpl<PermissionMapper, Permission>
    implements PermissionService {

    @Override
    public void reCache() {
        log.info("缓存权限点名称");
        List<Permission> list = this.findList(null);

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        Map<String, String> map = list.stream()
            .collect(Collectors.toMap(Permission::getPath, Permission::getPermissionName));

        RedisUtil.hmset(CacheConsts.PERM_NAME_KEY, map);
    }

    @Override
    public List<PermissionResp> findUserPermList(Long userId) {
        log.info("用户所有权限. userId={}", userId);
        return mapper.findUserPermList(userId);
    }

    @Override
    public int deleteByPid(Long pid) {
        log.info("删除pid权限. pid={}", pid);
        return mapper.deleteByPid(pid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Permission permission) {
        log.info("删除权限. permission={}", permission);

        mapper.deleteByPrimaryKey(permission.getId());
        // 删除下级权限
        mapper.deleteByPid(permission.getId());

        log.info("删除权限成功");
    }

}
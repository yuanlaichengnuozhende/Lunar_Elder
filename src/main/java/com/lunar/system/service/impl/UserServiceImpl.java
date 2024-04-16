package com.lunar.system.service.impl;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.mybatis.utils.RespUtil;
import com.lunar.common.security.auth.AuthUtil;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.User;
import com.lunar.system.entity.UserOrg;
import com.lunar.system.entity.UserRole;
import com.lunar.system.mapper.RoleMapper;
import com.lunar.system.mapper.UserMapper;
import com.lunar.system.mapper.UserOrgMapper;
import com.lunar.system.mapper.UserRoleMapper;
import com.lunar.system.query.RoleQuery;
import com.lunar.system.query.UserQuery;
import com.lunar.system.request.UserOrgReq;
import com.lunar.system.request.UserReq;
import com.lunar.system.response.UserResp;
import com.lunar.system.service.UserService;
import com.github.pagehelper.Page;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.core.utils.PageUtil;
import com.lunar.system.response.UserResp;
import com.lunar.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User>
    implements UserService {

    @Value("${reset.password}")
    private String resetPassword;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserOrgMapper userOrgMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public IPage<UserResp> findUserPage(UserQuery query, int pageNum, int pageSize, String order) {
        log.info("查询用户列表-带组织及角色信息. query={}, pageNum={}, pageSize={}, order={}", query, pageNum, pageSize,
                 order);

        PageUtil.startPage(pageNum, pageSize, order);
        List<UserResp> list = mapper.findUserPage(query);

        Page<UserResp> result = (Page<UserResp>) list;

        IPage<UserResp> page = new IPage<>(result);

        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User add(UserReq req) {
        log.info("新增用户. req={}", req);

        Long companyId = SecurityUtils.getCompanyId();

        // 校验用户名
        int count = this.count(UserQuery.builder()
                                   .companyId(companyId)
                                   .username(req.getUsername())
                                   .deleted(false)
                                   .build());
        if (count > 0) {
            throw new ServiceException(SystemCode.USER_NAME_EXIST);
        }

        Long userId = SecurityUtils.getLoginUser().getUserId();

        User user = CopyUtil.copyObject(req, User.class);

        user.setCompanyId(companyId);
        user.setPassword(AuthUtil.encryptPassword(resetPassword));
        user.setDefaultPassword(true);
        user.setId(null);
        user.setCreateBy(userId);
        user.setUpdateBy(userId);
        this.insertSelective(user);
        log.info("新增用户完成");

        //用户组织关系
//        List<Long> orgIdList = Splitter.on(",").splitToList(req.getOrgs());
        List<Long> orgIdList = Splitter.on(",")
            .splitToList(req.getOrgs())
            .stream()
            .filter(StringUtils::isNotBlank)
            .map(Long::parseLong)
            .collect(Collectors.toList());
        for (Long orgId : orgIdList) {
            UserOrg ug = new UserOrg();
            ug.setOrgId(orgId);
            ug.setUserId(user.getId());
            ug.setCreateBy(userId);
            userOrgMapper.insertSelective(ug);
        }

        //用户角色关系
        List<Long> roleIdList = Splitter.on(",")
            .splitToList(req.getRoles())
            .stream()
            .filter(StringUtils::isNotBlank)
            .map(Long::parseLong)
            .collect(Collectors.toList());
        for (Long roleId : roleIdList) {
            UserRole ur = new UserRole();
            ur.setRoleId(roleId);
            ur.setUserId(user.getId());
            ur.setCreateBy(userId);
            userRoleMapper.insertSelective(ur);
        }

        log.info("新增用户角色关系完成");

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User edit(UserReq req) {
        log.info("编辑用户. req={}", req);

        User user = this.selectOne(
            UserQuery.builder().id(req.getId()).companyId(SecurityUtils.getCompanyId()).deleted(false).build());
        if (user == null) {
            throw new ServiceException(ApiCode.DATA_ERROR);
        }

        // 用户名校验
        if (!StringUtils.equals(user.getUsername(), req.getUsername())) {
            int count = this.count(UserQuery.builder().username(req.getUsername()).deleted(false).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.USER_NAME_EXIST);
            }
        }

        Long userId = SecurityUtils.getLoginUser().getUserId();

        user.setMobile(req.getMobile());
        user.setRealName(req.getRealName());
        user.setUpdateBy(userId);
        this.updateSelective(user);
        log.info("修改用户完成");

        //用户组织关系
//        List<Long> orgIdList = Splitter.on(",").splitToList(req.getOrgs());
        List<Long> orgIdList = Splitter.on(",")
            .splitToList(req.getOrgs())
            .stream()
            .filter(StringUtils::isNotBlank)
            .map(Long::parseLong)
            .collect(Collectors.toList());

        if (orgIdList.size() > 0) {
            userOrgMapper.deleteByUserId(user.getId());
            for (Long orgId : orgIdList) {
                UserOrg ug = new UserOrg();
                ug.setOrgId(orgId);
                ug.setUserId(user.getId());
                ug.setCreateBy(userId);
                userOrgMapper.insertSelective(ug);
            }
        }

        //用户角色关系
        List<Long> roleIdList = Splitter.on(",")
            .splitToList(req.getRoles())
            .stream()
            .filter(StringUtils::isNotBlank)
            .map(Long::parseLong)
            .collect(Collectors.toList());

        if (roleIdList.size() > 0) {
            userRoleMapper.deleteByUserId(user.getId());
            for (Long roleId : roleIdList) {
                UserRole ur = new UserRole();
                ur.setRoleId(roleId);
                ur.setUserId(user.getId());
                ur.setCreateBy(userId);
                userRoleMapper.insertSelective(ur);
            }
        }

        log.info("修改用户角色关系完成");

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editRole(Long userId, List<Long> idList) {
        log.info("编辑用户角色. userId={}, idList={}", userId, idList);

        // 校验角色id
        if (CollectionUtils.isNotEmpty(idList)) {
            int count = roleMapper.count(RoleQuery.builder().idList(idList).deleted(false).build());
            if (count != idList.size()) {
                throw new ServiceException(ApiCode.DATA_ERROR);
            }
        }

        Long createBy = SecurityUtils.getUserId();
        userRoleMapper.deleteByUserId(userId);

        if (CollectionUtils.isEmpty(idList)) {
            log.info("编辑角色完成");
            return;
        }

        Date now = new Date();
        List<UserRole> entityList = idList.stream()
            .map(x -> UserRole.builder().userId(userId).roleId(x).createBy(createBy).createTime(now).build())
            .collect(Collectors.toList());

        userRoleMapper.batchInsert(entityList);
        log.info("编辑用户角色完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editOrg(Long userId, UserOrgReq req) {
        log.info("编辑用户组织. userId={}, req={}", userId, req);

        Long createBy = SecurityUtils.getUserId();

        userOrgMapper.deleteByUserId(userId);

        // 创建用户组织关系
        buildRel(req.getAllCheckedList(), req.getHalfCheckedList(), userId, createBy);

        log.info("编辑用户组织完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(User user) {
        log.info("删除用户. user={}", user);

        user.setDeleted(true);
        user.setUpdateBy(SecurityUtils.getUserId());
        this.update(user);

        userRoleMapper.deleteByUserId(user.getId());
        userOrgMapper.deleteByUserId(user.getId());

        // 踢出用户
        AuthUtil.logoutByUserId(user.getCompanyId(), user.getId());
        log.info("删除用户成功");
    }

    @Override
    public void updateLastLoginTime(Long id, Date date) {
        log.info("更新上次登录时间. id={}, date={}", id, date);
        mapper.updateLastLoginTime(id, date);
    }

    @Override
    public void disableUserNonRole() {
        log.info("禁用没有角色的用户");
        mapper.disableUserNonRole();
    }

    /**
     * 查询page中的createBy->用户名 map
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> Map<Long, String> getCreateByMap(List<T> list) {
        List<Long> ids = RespUtil.getCreateByList(list);
        return getUserIdMap(ids);
    }

    /**
     * 查询page中的updateBy->用户名 map
     *
     * @param list
     * @param <T>
     * @return
     */
    @Override
    public <T> Map<Long, String> getUpdateByMap(List<T> list) {
        List<Long> ids = RespUtil.getUpdateByList(list);
        return getUserIdMap(ids);
    }

    /**
     * 根据id查询对应用户名
     *
     * @param ids
     * @return
     */
    @Override
    public Map<Long, String> getUserIdMap(List<Long> ids) {
        Map<Long, String> map = Maps.newHashMap();
        if (CollectionUtils.isEmpty(ids)) {
            return map;
        }

        try {
            List<User> userList = this.findList(UserQuery.builder().idList(ids).build());
            Map<Long, User> userMap = ConvertUtil.list2Map(userList, User::getId);
            if (MapUtils.isEmpty(userMap)) {
                return map;
            }

            return userMap.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getRealName()));
        } catch (Exception e) {
            log.error("根据id查询对应用户名 error", e);
        }

        return map;
    }

    /**
     * 创建用户组织关系
     *
     * @param allCheckedList
     * @param halfCheckedList
     */
    private void buildRel(List<Long> allCheckedList, List<Long> halfCheckedList, Long userId, Long createBy) {
        List<UserOrg> entityList = Lists.newArrayList();
        Date now = new Date();
        if (CollectionUtils.isNotEmpty(allCheckedList)) {
            allCheckedList.forEach(x -> {
                UserOrg userOrg = UserOrg.builder()
                    .userId(userId)
                    .orgId(x)
                    .optionType(OptionType.ALL)
                    .createBy(createBy)
                    .createTime(now)
                    .build();
                entityList.add(userOrg);
            });
        }
        if (CollectionUtils.isNotEmpty(halfCheckedList)) {
            halfCheckedList.forEach(x -> {
                UserOrg userOrg = UserOrg.builder()
                    .userId(userId)
                    .orgId(x)
                    .optionType(OptionType.HALF)
                    .createBy(createBy)
                    .createTime(now)
                    .build();
                entityList.add(userOrg);
            });
        }

        if (CollectionUtils.isNotEmpty(entityList)) {
            userOrgMapper.batchInsert(entityList);
        }
    }

}
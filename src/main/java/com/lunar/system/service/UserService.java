package com.lunar.system.service;

import com.lunar.common.core.domain.IPage;
import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.User;
import com.lunar.system.query.UserQuery;
import com.lunar.system.request.UserOrgReq;
import com.lunar.system.request.UserReq;
import com.lunar.system.response.UserResp;
import com.lunar.common.core.domain.IPage;
import com.lunar.system.response.UserResp;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService extends BaseService<User> {

    /**
     * 查询用户列表-带组织及角色信息
     *
     * @param query
     * @param pageNum
     * @param pageSize
     * @param order
     * @return
     */
    IPage<UserResp> findUserPage(UserQuery query, int pageNum, int pageSize, String order);

    /**
     * 新增用户
     *
     * @param req
     * @return
     */
    User add(UserReq req);

    /**
     * 编辑用户
     *
     * @param req
     * @return
     */
    User edit(UserReq req);

    /**
     * 编辑用户角色
     *
     * @param userId
     * @param idList
     */
    void editRole(Long userId, List<Long> idList);

    /**
     * 编辑用户组织
     *
     * @param userId
     * @param req
     */
    void editOrg(Long userId, UserOrgReq req);

    /**
     * 删除用户
     *
     * @param user
     */
    void delete(User user);

    /**
     * 禁用没有角色的用户
     */
    void disableUserNonRole();

    /**
     * 更新上次登录时间
     *
     * @param id
     * @param date
     */
    void updateLastLoginTime(Long id, Date date);

    /**
     * 查询page中的createBy->用户名 map
     *
     * @param list
     * @param <T>
     * @return
     */
    <T> Map<Long, String> getCreateByMap(List<T> list);

    /**
     * 查询page中的updateBy->用户名 map
     *
     * @param list
     * @param <T>
     * @return
     */
    <T> Map<Long, String> getUpdateByMap(List<T> list);

    /**
     * 根据id查询对应用户名
     *
     * @param ids
     * @return
     */
    Map<Long, String> getUserIdMap(List<Long> ids);

}

package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.User;
import com.lunar.system.query.UserQuery;
import com.lunar.system.response.UserResp;
import com.lunar.system.response.UserResp;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询用户列表-带组织及角色信息
     *
     * @param query
     * @return
     */
    List<UserResp> findUserPage(UserQuery query);

    /**
     * 更新上次登录时间
     *
     * @param id
     * @param date
     */
    void updateLastLoginTime(@Param("id") Long id, @Param("date") Date date);

    /**
     * 禁用没有角色的用户
     */
    void disableUserNonRole();

}
package com.lunar.system.mapper;

import com.lunar.common.mybatis.mapper.BaseMapper;
import com.lunar.system.entity.UserOrg;

public interface UserOrgMapper extends BaseMapper<UserOrg> {

    /**
     * @param userId
     * @return
     */
    int deleteByUserId(Long userId);

    /**
     * @param orgId
     * @return
     */
    int deleteByOrgId(Long orgId);

}
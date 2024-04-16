package com.lunar.system.service;

import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.request.PageParam;
import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.Info;
import com.lunar.system.enums.InfoStatus;
import com.lunar.system.request.InfoReq;
import com.lunar.common.core.domain.IPage;
import com.lunar.common.core.request.PageParam;

public interface InfoService extends BaseService<Info> {

    /**
     * 资讯分页列表
     *
     * @param pageParam
     * @param likeTitle
     * @param infoType
     * @param infoStatus
     * @param order
     * @return
     */
    IPage<Info> page(PageParam pageParam, String likeTitle, String infoType, InfoStatus infoStatus, String order);

    /**
     * 新增资讯
     *
     * @param req
     * @return
     */
    Info add(InfoReq req);

    /**
     * 编辑资讯
     *
     * @param req
     * @return
     */
    Info edit(InfoReq req);

}

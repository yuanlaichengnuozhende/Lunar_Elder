package com.lunar.system.service;

import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.Factor;
import com.lunar.system.request.FactorReq;

public interface FactorService extends BaseService<Factor> {

    /**
     * 新增排放因子
     *
     * @param req
     * @return
     */
    Factor add(FactorReq req);

    /**
     * 编辑排放因子
     *
     * @param req
     * @return
     */
    Factor edit(FactorReq req);

}

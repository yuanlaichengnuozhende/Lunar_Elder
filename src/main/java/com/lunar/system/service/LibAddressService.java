package com.lunar.system.service;

import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.LibAddress;

import java.util.List;

public interface LibAddressService extends BaseService<LibAddress> {

    /**
     * 查询地址层级，从顶级节点到当前节点
     *
     * @param code
     * @return
     */
    List<LibAddress> findAddressHierarchy(Integer code);

    /**
     * 查询地址层级。xx省xx市xx区
     *
     * @param code
     * @param separator 分隔符
     * @return
     */
    String findAddressHierarchyStr(Integer code, String separator);

}

package com.lunar.system.service;

import com.lunar.common.mybatis.service.BaseService;
import com.lunar.system.entity.Org;
import com.lunar.system.request.OrgReq;

import java.util.List;
import java.util.Map;

public interface OrgService extends BaseService<Org> {

    /**
     * 查询组织层级，从顶级节点到当前节点
     *
     * @param companyId
     * @param orgId 尾节点
     * @return
     */
    List<Org> findOrgHierarchy(Long companyId, Long orgId);

    /**
     * 所有节点的组织结构
     *
     * @param companyId
     * @return key：orgId，value：从顶点到当前orgId的路径
     */
    Map<Long, List<Org>> allHierarchyMap(Long companyId);

    /**
     * 所有节点的组织树
     *
     * @param companyId
     * @param separator
     * @return key：orgId，value：从顶点到当前orgId的组织名，用separator连接
     */
    Map<Long, String> allHierarchyTree(Long companyId, String separator);

    /**
     * 查询所有子节点（包含自身）
     *
     * @param companyId
     * @param orgId
     * @return
     */
    List<Org> findAllChildren(Long companyId, Long orgId);

    /**
     * 查询所有子节点id
     *
     * @param companyId
     * @param orgId
     * @return
     */
    List<Long> findAllChildrenId(Long companyId, Long orgId);

    /**
     * 新增组织
     *
     * @param req
     * @return
     */
    Org add(OrgReq req);

    /**
     * 编辑组织
     *
     * @param req
     * @return
     */
    Org edit(OrgReq req);

    /**
     * 删除组织
     *
     * @param role
     */
    void delete(Org role);

}

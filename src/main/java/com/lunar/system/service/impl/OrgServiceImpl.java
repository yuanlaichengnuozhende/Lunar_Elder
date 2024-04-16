package com.lunar.system.service.impl;

import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.enums.OrgType;
import com.lunar.common.core.exception.ServiceException;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.common.mybatis.service.impl.BaseServiceImpl;
import com.lunar.common.security.utils.SecurityUtils;
import com.lunar.system.entity.Org;
import com.lunar.system.mapper.OrgMapper;
import com.lunar.system.mapper.UserOrgMapper;
import com.lunar.system.query.OrgQuery;
import com.lunar.system.query.UserOrgQuery;
import com.lunar.system.request.OrgReq;
import com.lunar.system.service.OrgService;
import com.lunar.system.service.UserOrgService;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lunar.common.core.code.ApiCode;
import com.lunar.common.core.code.SystemCode;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.core.enums.OptionType;
import com.lunar.common.core.enums.OrgType;
import com.lunar.common.core.utils.ConvertUtil;
import com.lunar.common.core.utils.CopyUtil;
import com.lunar.system.service.OrgService;
import com.lunar.system.service.UserOrgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrgServiceImpl extends BaseServiceImpl<OrgMapper, Org>
    implements OrgService {

    @Autowired
    private UserOrgService userOrgService;
    @Autowired
    private UserOrgMapper userOrgMapper;

    @Override
    public List<Org> findOrgHierarchy(Long companyId, Long orgId) {
        log.info("查询组织层级，从顶级节点到当前节点. companyId={}, orgId={}", companyId, orgId);

        List<Org> list = this.findList(
            OrgQuery.builder().companyId(companyId).deleted(false).build());

        List<Org> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(list)) {
            log.info("查询应用组织为空");
            return result;
        }

        Map<Long, Org> map = ConvertUtil.list2Map(list, Org::getId);

        // 尾节点
        Org end = map.get(orgId);
        if (end == null) {
            log.info("查询组织结构为空");
            return result;
        }
        result.add(end);

        // 改为org_path查询
        int level = 10;
        while (end.getPid() != 0) {
            end = map.get(end.getPid());
            if (end == null || level <= 0) {
                break;
            }
            result.add(end);
            level--;
        }

        return Lists.reverse(result);
    }

    @Override
    public Map<Long, List<Org>> allHierarchyMap(Long companyId) {
        log.info("所有节点的组织结构.companyId={}", companyId);
        Map<Long, List<Org>> orgTreeMap = Maps.newHashMap();

        // 查询所有
        List<Org> list = this.findList(OrgQuery.builder().companyId(companyId).deleted(false).build());
        if (CollectionUtils.isEmpty(list)) {
            return orgTreeMap;
        }

        Map<Long, Org> orgIdMap = ConvertUtil.list2Map(list, Org::getId);

        list.stream().forEach(y -> {
            List<Org> hierarchy = hierarchy(orgIdMap, y);
            orgTreeMap.put(y.getId(), hierarchy);
        });

        return orgTreeMap;
    }

    @Override
    public Map<Long, String> allHierarchyTree(Long companyId, String separator) {
        log.info("所有节点的组织树. companyId={}, separator={}", companyId, separator);
        Map<Long, String> orgTree = Maps.newHashMap();
        Map<Long, List<Org>> orgMap = allHierarchyMap(companyId);
        if (MapUtils.isEmpty(orgMap)) {
            return orgTree;
        }

        orgMap.forEach((k, v) -> {
            String tree = Joiner.on(separator)
                .join(v.stream().map(Org::getOrgName).collect(Collectors.toList()));
            orgTree.put(k, tree);
        });

        return orgTree;
    }

    @Override
    public List<Org> findAllChildren(Long companyId, Long orgId) {
        log.info("查询所有子节点. companyId={}, orgId={}", companyId, orgId);

        List<Org> list = Lists.newArrayList();

//        OrgQuery query = OrgQuery.builder()
//            .companyId(SecurityUtils.getCompanyId())
//            .pid(orgId)
//            .deleted(false)
//            .build();
//        List<Org> orgList = this.findList(query);
//        list.addAll(orgList);
//
//        for (Org org : orgList) {
//            List<Org> children = findAllChildren(org.getId());
//            list.addAll(children);
//        }

        Org org = this.selectOne(OrgQuery.builder().id(orgId).companyId(companyId).deleted(false).build());
        if (org == null) {
            return list;
        }

        OrgQuery query = OrgQuery.builder()
            .companyId(companyId)
            .likeOrgPath(org.getOrgPath())
            .deleted(false)
            .build();
        return this.findList(query, "pid asc, id asc");
    }

    @Override
    public List<Long> findAllChildrenId(Long companyId, Long orgId) {
        log.info("查询所有子节点id. companyId={}, orgId={}", companyId, orgId);
        List<Org> list = findAllChildren(companyId, orgId);
        return list.stream().map(Org::getId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Org add(OrgReq req) {
        log.info("新增组织. req={}", req);

        // 校验name
        int count = this.count(OrgQuery.builder().orgName(req.getOrgName()).deleted(false).build());
        if (count > 0) {
            throw new ServiceException(SystemCode.ORG_NAME_EXIST);
        }

        // 校验组织编码
        int countCode = this.count(OrgQuery.builder().orgCode(req.getOrgCode()).deleted(false).build());
        if (countCode > 0) {
            throw new ServiceException(SystemCode.ORG_CODE_EXIST);
        }

        // 校验组织简称
        int countAddr = this.count(OrgQuery.builder().orgAbbr(req.getOrgAbbr()).deleted(false).build());
        if (countAddr > 0) {
            throw new ServiceException(SystemCode.ORG_ADDR_EXIST);
        }

        // 上级组织
        Org pOrg = this.selectByPrimaryKey(req.getPid());
        if (pOrg == null || pOrg.getDeleted()) {
            throw new ServiceException(SystemCode.ORG_PARENT_NOT_FOUND);
        }
        if (SecurityUtils.missCompany(pOrg.getCompanyId())) {
            throw new ServiceException(ApiCode.DATA_PERMISSION_ERROR);
        }
        // 单体组织不能新增子组织
        if (pOrg.getOrgType() == OrgType.SINGLE) {
            throw new ServiceException(SystemCode.ORG_SINGLE_CANNOT_ADD);
        }

//        // 传入的pid和orgType校验
//        if (req.getOrgType().getLevel() - pOrg.getOrgType().getLevel() != 1) {
//            throw new ServiceException(SystemCode.ORG_PARENT_ERROR);
//        }

//        Long userId = SecurityUtils.getUserId();
        Long userId = SecurityUtils.getLoginUser().getUserId();

        Org org = CopyUtil.copyObject(req, Org.class);
        org.setId(null);
        org.setCompanyId(SecurityUtils.getCompanyId());
        org.setCreateBy(userId);
        org.setUpdateBy(userId);
        this.insertSelective(org);
        log.info("新增组织完成");

        // 生成orgPath
        String orgPath = pOrg.getOrgPath() + org.getId() + "/";
        org.setOrgPath(orgPath);
        this.updateSelective(org);
        log.info("更新组织path完成");

        return org;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Org edit(OrgReq req) {
        log.info("编辑组织. req={}", req);

        Org org = this.selectOne(
            OrgQuery.builder().id(req.getId()).companyId(SecurityUtils.getCompanyId()).deleted(false).build());
        if (org == null) {
            throw new ServiceException(ApiCode.DATA_ERROR);
        }

        // 新组织名校验
        if (!StringUtils.equals(org.getOrgName(), req.getOrgName())) {
            int count = this.count(OrgQuery.builder().orgName(req.getOrgName()).deleted(false).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.ORG_NAME_EXIST);
            }
        }

        // 新组织编码验
        if (!StringUtils.equals(org.getOrgCode(), req.getOrgCode())) {
            int count = this.count(OrgQuery.builder().orgCode(req.getOrgCode()).deleted(false).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.ORG_CODE_EXIST);
            }
        }

        // 新组织简称验
        if (!StringUtils.equals(org.getOrgAbbr(), req.getOrgAbbr())) {
            int count = this.count(OrgQuery.builder().orgAbbr(req.getOrgAbbr()).deleted(false).build());
            if (count > 0) {
                throw new ServiceException(SystemCode.ORG_ADDR_EXIST);
            }
        }

//        Long userId = SecurityUtils.getUserId();
        Long userId = SecurityUtils.getLoginUser().getUserId();

        // 忽略pid及orgType变更
        org.setOrgName(req.getOrgName());
        org.setOrgCode(req.getOrgCode());
        org.setOrgAbbr(req.getOrgAbbr());
        org.setOrgInfo(req.getOrgInfo());
        org.setUpdateBy(userId);
        this.updateSelective(org);

        log.info("编辑组织完成");

        return org;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Org org) {
        log.info("删除组织. org={}", org);

        // 请先删除所有下级组织再进行删除组织操作
        int orgCount = this.count(OrgQuery.builder().pid(org.getId()).deleted(false).build());
        if (orgCount > 0) {
            throw new ServiceException(SystemCode.ORG_HAS_CHILD);
        }

        // 请先删除组织下所有用户，再进行删除组织操作
        int userCount = userOrgService.count(
            UserOrgQuery.builder().orgId(org.getId()).optionType(OptionType.ALL).build());
        if (userCount > 0) {
            throw new ServiceException(SystemCode.ORG_HAS_USER);
        }

        // 删除用户关系
        userOrgMapper.deleteByOrgId(org.getId());

        org.setDeleted(true);
        org.setUpdateBy(SecurityUtils.getUserId());
        this.updateSelective(org);
        log.info("删除组织成功");
    }

    /**
     * 查询组织层级，从顶级节点到当前节点
     *
     * @param orgIdMap
     * @param org
     * @return
     */
    private List<Org> hierarchy(Map<Long, Org> orgIdMap, Org org) {
        List<Org> list = Lists.newArrayList(org);
        if (org.getPid() == 0) {
            return list;
        }

        // orgPath分隔
        List<Long> orgIdList = Splitter.on(Consts.ORG_SEPARATOR)
            .splitToList(org.getOrgPath())
            .stream()
            .filter(StringUtils::isNotBlank)
            .map(Long::parseLong)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(orgIdList)) {
            return list;
        }

        list = orgIdList.stream().map(x -> {
            Org orgDto = orgIdMap.get(x);
            if (ObjectUtils.isEmpty(orgDto)) {
                throw new ServiceException(SystemCode.ORG_ERROR);
            }

            return orgDto;
        }).collect(Collectors.toList());

        return list;
    }

}